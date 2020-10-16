package com.ahoubouby.semigroup_monoid

trait Monoid[A] extends SemiGroup[A] {
  def empty: A
}
