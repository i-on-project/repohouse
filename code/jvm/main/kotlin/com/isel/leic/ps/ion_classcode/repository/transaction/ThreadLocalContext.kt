package com.isel.leic.ps.ion_classcode.repository.transaction

/**
 * Holds the thread local instance which is used to store and obtain the current transaction.
 */
class ThreadLocalContext {
    companion object {

        private val context = ThreadLocal<Transaction>()

        fun set(transaction: Transaction) {
            context.set(transaction)
        }

        fun get(): Transaction? = context.get()

        fun remove() {
            context.remove()
        }
    }
}
