package ca.danielstout.halicrime

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.companionObject

fun <R : Any> R.logger(): Lazy<Logger>
{
    return lazy { LoggerFactory.getLogger(unwrapCompanionClass(this.javaClass).name) }
}

fun <T : Any> unwrapCompanionClass(ofClass: Class<T>): Class<*>
{
    return if (ofClass.enclosingClass != null && ofClass.enclosingClass.kotlin.companionObject?.java == ofClass)
    {
        ofClass.enclosingClass
    }
    else
    {
        ofClass
    }
}