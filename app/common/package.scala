package com.flashjob

package object common {
  object Env extends Enumeration {
    type Env = Value
    val Local, Dev, Test, Staging, Prod = Value
  }
}
