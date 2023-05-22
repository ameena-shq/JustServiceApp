package com.uni.justservices.data

import com.uni.justservices.R

enum class UserType {
    Student {
        override fun getName(): Int {
            return R.string.student
        }

        override fun getID(): Int {
            return 1
        }
    },
    Expert{
        override fun getName(): Int {
            return R.string.expert
        }

        override fun getID(): Int {
            return 2
        }
    },
    BusDriver{
        override fun getName(): Int {
            return R.string.bus_driver
        }

        override fun getID(): Int {
            return 3
        }
    },
    Non{
        override fun getName(): Int {
            return 0
        }

        override fun getID(): Int {
            return 0
        }
    };

    abstract fun getName():Int
    abstract fun getID():Int
}