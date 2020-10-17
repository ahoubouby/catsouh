package com.ahoubouby.semigroup_monoid

trait SemiGroup[A] {
  def combine(x: A, y: A): A
}

object SemiGroup {}
