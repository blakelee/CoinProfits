package net.blakelee.coinprofits.base

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

//This code written by Ihor Kucherenko as part of Android-Architecture-Components

@Suppress("LeakingThis")
abstract class BaseLifecycleActivity<T : AndroidViewModel> : AppCompatActivity(), LifecycleRegistryOwner {

    abstract val viewModelClass: Class<T>
    protected lateinit var viewModel: T
    private val registry = LifecycleRegistry(this)
    override fun getLifecycle(): LifecycleRegistry = registry
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(viewModelClass)
    }
}