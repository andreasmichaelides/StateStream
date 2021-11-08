package com.bitatron.statestream.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bitatron.statestream.logger.Logger
import com.bitatron.statestream.schedulers.SchedulersProvider
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlin.system.measureTimeMillis

abstract class StateViewModel<T>(
    initialUiModel: T,
    internal open val logger: Logger,
    private val schedulersProvider: SchedulersProvider
) : ViewModel() where T : UiModel {

    protected val subscriptions = CompositeDisposable()
    val input = PublishSubject.create<Input<T>>()
    private val viewModelAction = PublishSubject.create<ViewModelAction>()
    private val dataViewModelAction = PublishSubject.create<ViewModelActionData<T>>()

    // The final output of the UiModel that will represent the Activity data/state. This is where Activities will observe and get their data
    private val activityUiModel = MutableLiveData<T>()

    init {
        // The main stream where all the processing happens, transformation from the previous state to the next, depending in the
        // input that is emitted in the stream. The transformation and processing happens in the scan() operator of the stream
        // If any debugging is needed, it can be easily observed here, as everything is centralised at this part
        subscriptions.add(
            input.toFlowable(BackpressureStrategy.LATEST)
                .onBackpressureLatest()
                .observeOn(schedulersProvider.single())
                .onBackpressureLatest()
                .map<State> { it }
                // The initial model is need to start the stream, it should be also the starting state of the Activity
                // The scan operator requires a previous and new model to work and make any transformations, so the initial UiModel
                // will skip the scan operator and be emitted directly at the subscribe() function
                .startWith(initialUiModel)
                .scan { t1: State, t2: State -> transformModel(t1, t2) }
                .map { it as T }
                .subscribeOn(schedulersProvider.single())
                .onBackpressureLatest()
                .observeOn(schedulersProvider.mainThread())
                .onBackpressureLatest()
                .onErrorResumeNext(Flowable.empty())
                .subscribe({
                    activityUiModel.value = it
                    executeViewModelAction(it)
                }, { logger.e(this, it) })
        )
    }

    override fun onCleared() {
        super.onCleared()
        subscriptions.clear()
    }

    /**
     * Transforms the previous UiModel to the the next, depending on the implementation of the transformState() function on each Input<T>
     * toModel that is emitted in the stream
     */
    private fun transformModel(fromModel: State, toModel: State): State =
        try {
            when (toModel) {
                is Input<*> -> {
                    val input = toModel as Input<T>
                    var transformedState: T? = null
                    val measuredTimeMillis: Long =
                        measureTimeMillis { transformedState = input.transformState(fromModel as T) }
                    transformedState!!.timeToExecute = measuredTimeMillis
                    transformedState!!
                }
                else -> {
                    logger.e(this, "New model $toModel does not inherit Input, returning old model")
                    fromModel
                }
            }
        } catch (e: Throwable) {
            logger.e(this, e.message.orEmpty())
            when (fromModel) {
                is UiModel -> fromModel.push(StateViewModelError(e))
                else -> fromModel
            }
        }

    /**
     * Finds any ViewContainerActions that need to be executed after the emission of a new UiModel and sends it to the appropriate action stream
     */
    private fun executeViewModelAction(uiModel: T) {
        while (uiModel.viewModelActions.isNotEmpty()) {
            when (val action = uiModel.viewModelActions.pop()) {
                is DataViewModelAction -> dataViewModelAction.onNext(
                    ViewModelActionData(
                        uiModel,
                        action
                    )
                )
                is ViewModelAction -> viewModelAction.onNext(action)
            }
        }
    }

    fun input(): Observer<Input<T>> = input

    fun viewModelAction(): Observable<ViewModelAction> = viewModelAction.subscribeOn(schedulersProvider.computation())

    fun dataViewModelAction(): Observable<ViewModelActionData<T>> = dataViewModelAction.subscribeOn(schedulersProvider.computation())

    fun activityUiModel(): LiveData<T> = activityUiModel
}