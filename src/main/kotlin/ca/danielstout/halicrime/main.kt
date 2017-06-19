package ca.danielstout.halicrime

import com.fasterxml.jackson.databind.ObjectMapper

fun main(args: Array<String>)
{
    val c = CrimeFetcher(ObjectMapper())
    App()
}