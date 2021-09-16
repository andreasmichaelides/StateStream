package com.bitatron.statestream.presentation

import java.util.*

interface State

interface Input<T> : State where T : UiModel {
    fun transformState(uiModel: T): T
}

open class UiModel(
    open val errors: Stack<Error>,
    open val viewModelActions: Stack<ViewModelAction>,
    open val activityActions: MutableList<ActivityAction>,
    open var timeToExecute: Long = 0
) : State

open class Error
interface ViewModelAction
interface DataViewModelAction : ViewModelAction
interface ActivityAction

@Deprecated("Use the new <T> T.pushError(error: Error) instead")
fun UiModel.pushError(error: Error): Stack<Error> {
    errors.push(error)
    return errors
}

@Deprecated("Use the new <T> T.pushViewModelAction(viewModelAction: ViewModelAction) instead")
fun UiModel.pushAction(viewModelAction: ViewModelAction): Stack<ViewModelAction> {
    viewModelActions.push(viewModelAction)
    return viewModelActions
}

@Deprecated("Fie to")
fun <T> T.pushViewModelAction(viewModelAction: ViewModelAction): T where T : UiModel {
    viewModelActions.push(viewModelAction)
    return this
}

@Deprecated("Use the new <T> T.pushError(error: Error) instead")
fun UiModel.pushActivityAction(activityAction: ActivityAction): List<ActivityAction> {
    activityActions.add(activityAction)
    return activityActions
}

fun <T> T.push(activityAction: ActivityAction): T where T : UiModel {
    activityActions.add(activityAction)
    return this
}

fun <T> T.push(viewModelAction: ViewModelAction): T where T : UiModel {
    viewModelActions.push(viewModelAction)
    return this
}

fun <T> T.push(error: Error): T where T : UiModel {
    errors.push(error)
    return this
}

fun <T> Stack<T>.popAll(execute: (item: T) -> Unit) {
    while (isNotEmpty()) {
        execute(pop())
    }
}
