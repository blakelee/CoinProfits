package net.blakelee.coinprofits.fragments

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.support.v4.app.Fragment

class DetailsFragment : Fragment(), LifecycleRegistryOwner {





    private val registry = LifecycleRegistry(this)
    override fun getLifecycle(): LifecycleRegistry = registry
}