package com.example.moodymusicforandroid
import org.junit.Test
import androidx.activity.ComponentActivity
import androidx.navigationevent.NavigationEventDispatcherOwner
class NavigationOwnerTest { @Test fun testOwner() { println("IS_OWNER: " + NavigationEventDispatcherOwner::class.java.isAssignableFrom(ComponentActivity::class.java)) } }
