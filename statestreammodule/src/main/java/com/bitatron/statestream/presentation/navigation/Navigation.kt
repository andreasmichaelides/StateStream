package com.bitatron.statestream.presentation.navigation

import android.app.Activity
import android.view.MenuItem
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView

interface NavigationAction

interface Navigation {

    fun setupWithBottomNavigationView(activity: Activity, bottomNavigationView: BottomNavigationView, hostViewId: Int)

    fun onMenuItemSelected(activity: Activity, menuItem: MenuItem, hostViewId: Int): Boolean

    fun navigate(view: View, navigationAction: NavigationAction)

    fun navigate(view: View, navigationDirections: NavigationDirections)

}

open class NavigationDirections