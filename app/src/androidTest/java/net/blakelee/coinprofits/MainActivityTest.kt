package net.blakelee.coinprofits

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import net.blakelee.coinprofits.activities.MainActivity
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule @JvmField
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun init() {
        activityRule.activity.supportFragmentManager.beginTransaction()
    }

    @Test
    fun checkViewsDisplay() {
        onView(withId(R.id.last_updated_layout)).check(matches(not(isDisplayed())))
    }
}