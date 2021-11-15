package com.meluzin.functional;

public class T {
	public static <A> V1<A> V(A a) {
		return new V1<A>(a);
	}
	public static <A, B> V2<A, B> V(A a, B b) {
		return new V2<A, B>(a, b);
	}
	public static <A, B, C> V3<A, B, C> V(A a, B b, C c) {
		return new V3<A, B, C>(a, b, c);
	}
	public static <A, B, C, D> V4<A, B, C, D> V(A a, B b, C c, D d) {
		return new V4<A, B, C, D>(a, b, c, d);
	}
	public static <A, B, C, D, E> V5<A, B, C, D, E> V(A a, B b, C c, D d, E e) {
		return new V5<A, B, C, D, E>(a, b, c, d, e);
	}
	public static <A, B> V2<A, B> copyAndAdd(V1<A> a, B b) {
		return new V2<A, B>(a.getA(), b);
	}
	public static <A, B, C> V3<A, B, C> copyAndAdd(V2<A, B> v, C c) {
		return new V3<A, B, C>(v.getA(), v.getB(), c);
	}
	public static <A, B, C, D> V4<A, B, C, D> copyAndAdd(V3<A, B, C> v, D d) {
		return new V4<A, B, C, D>(v.getA(), v.getB(), v.getC(), d);
	}
	public static <A, B, C, D, E> V5<A, B, C, D, E> copyAndAdd(V4<A, B, C, D> v, E e) {
		return new V5<A, B, C, D, E>(v.getA(), v.getB(), v.getC(), v.getD(), e);
	}
	@SuppressWarnings("unchecked")
	public static <A> int compareProp(A a, A oa) {
		if (a == oa) return 0;
		if (a == null) return 1;
		if (oa == null) return -1;
		if (a instanceof Comparable<?> && oa instanceof Comparable<?>) {
			return ((Comparable<A>)a).compareTo(oa);
		} else return a.toString().compareTo(oa.toString());
	}
	public static class V1<A> implements Comparable<Object> {
		private A a;
		public V1(A a) {
			this.a = a;
		}
		public A getA() {
			return a;
		}
		public V1<A> setA(A a) {
			this.a = a;
			return this;
		}
		@Override
		public String toString() {
			return "A="+a;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((a == null) ? 0 : a.hashCode());
			return result;
		}
		@SuppressWarnings("unchecked")
		@Override
		public int compareTo(Object o) {
			if (o == null) return -1;
			V1<A> oa;
			if (!(o instanceof V1<?>)) {
				return toString().compareTo(o.toString());
			} else {
				oa = (V1<A>)o;
			}
			int comparePropA = compareProp(getA(), oa.getA());
			if (comparePropA != 0) return comparePropA;
			return 0;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			V1<?> other = (V1<?>) obj;
			if (a == null) {
				if (other.a != null)
					return false;
			} else if (!a.equals(other.a))
				return false;
			return true;
		}	
		
		
		
	}
	public static class V2<A, B> extends V1<A>{
		private B b;
		public V2(A a, B b) {
			super(a);
			this.b = b;
		}
		public B getB() {
			return b;
		}
		public V2<A, B> setB(B b) {
			this.b = b;
			return this;
		}
		@SuppressWarnings("unchecked")
		@Override
		public V2<A, B> setA(A a) {
			return (V2<A, B>)super.setA(a);
		}
		@Override
		public String toString() {
			return super.toString() + ";B="+b;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((b == null) ? 0 : b.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			V2<?,?> other = (V2<?,?>) obj;
			if (b == null) {
				if (other.b != null)
					return false;
			} else if (!b.equals(other.b))
				return false;
			return true;
		}	
		@SuppressWarnings("unchecked")
		@Override
		public int compareTo(Object o) {
			if (o == null) return -1;
			V2<A,B> oa;
			if (!(o instanceof V2<?,?>)) {
				return toString().compareTo(o.toString());
			} else {
				oa = (V2<A,B>)o;
			}
			int comparePropA = compareProp(getA(), oa.getA());
			if (comparePropA != 0) return comparePropA;
			int comparePropB = compareProp(getB(), oa.getB());
			if (comparePropB != 0) return comparePropB;
			return 0;
		}
		
	}
	public static class V3<A, B, C> extends V2<A, B>{
		private C c;
		public V3(A a, B b, C c) {
			super(a, b);
			this.c = c;
		}
		public C getC() {
			return c;
		}
		public V3<A, B, C> setC(C c) {
			this.c = c;
			return this;
		}
		@SuppressWarnings("unchecked")
		@Override
		public V3<A, B, C> setA(A a) {
			return (V3<A, B, C>)super.setA(a);
		}
		@SuppressWarnings("unchecked")
		@Override
		public V3<A, B, C> setB(B b) {
			return (V3<A, B, C>)super.setB(b);
		}
		@Override
		public String toString() {
			return super.toString() + ";C="+c;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((c == null) ? 0 : c.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			V3<?,?,?> other = (V3<?,?,?>) obj;
			if (c == null) {
				if (other.c != null)
					return false;
			} else if (!c.equals(other.c))
				return false;
			return true;
		}
		@SuppressWarnings("unchecked")
		@Override
		public int compareTo(Object o) {
			if (o == null) return -1;
			V3<A,B,C> oa;
			if (!(o instanceof V3<?,?,?>)) {
				return toString().compareTo(o.toString());
			} else {
				oa = (V3<A,B,C>)o;
			}
			int comparePropA = compareProp(getA(), oa.getA());
			if (comparePropA != 0) return comparePropA;
			int comparePropB = compareProp(getB(), oa.getB());
			if (comparePropB != 0) return comparePropB;
			int comparePropC = compareProp(getC(), oa.getC());
			if (comparePropC != 0) return comparePropC;
			return 0;
		}
		
	}
	public static class V4<A, B, C, D> extends V3<A, B, C>{
		private D d;
		public V4(A a, B b, C c, D d) {
			super(a, b, c);
			this.d = d;
		}
		public D getD() {
			return d;
		}
		public V4<A, B, C, D> setD(D d) {
			this.d = d;
			return this;
		}
		@SuppressWarnings("unchecked")
		@Override
		public V4<A, B, C, D> setA(A a) {
			return (V4<A, B, C, D>)super.setA(a);
		}
		@SuppressWarnings("unchecked")
		@Override
		public V4<A, B, C, D> setB(B b) {
			return (V4<A, B, C, D>)super.setB(b);
		}
		@SuppressWarnings("unchecked")
		@Override
		public V4<A, B, C, D> setC(C c) {
			return (V4<A, B, C, D>)super.setC(c);
		}
		@Override
		public String toString() {
			return super.toString() + ";D="+d;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((d == null) ? 0 : d.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			V4<?,?,?,?> other = (V4<?,?,?,?>) obj;
			if (d == null) {
				if (other.d != null)
					return false;
			} else if (!d.equals(other.d))
				return false;
			return true;
		}
		@SuppressWarnings("unchecked")
		@Override
		public int compareTo(Object o) {
			if (o == null) return -1;
			V4<A,B,C,D> oa;
			if (!(o instanceof V4<?,?,?,?>)) {
				return toString().compareTo(o.toString());
			} else {
				oa = (V4<A,B,C,D>)o;
			}
			int comparePropA = compareProp(getA(), oa.getA());
			if (comparePropA != 0) return comparePropA;
			int comparePropB = compareProp(getB(), oa.getB());
			if (comparePropB != 0) return comparePropB;
			int comparePropC = compareProp(getC(), oa.getC());
			if (comparePropC != 0) return comparePropC;
			int comparePropD = compareProp(getD(), oa.getD());
			if (comparePropD != 0) return comparePropD;
			return 0;
		}
	}
	public static class V5<A, B, C, D, E> extends V4<A, B, C, D>{
		private E e;
		public V5(A a, B b, C c, D d, E e) {
			super(a, b, c, d);
			this.e = e;
		}
		public E getE() {
			return e;
		}
		public V5<A, B, C, D, E> setE(E e) {
			this.e = e;
			return this;
		}
		@SuppressWarnings("unchecked")
		@Override
		public V5<A, B, C, D, E> setA(A a) {
			return (V5<A, B, C, D, E>)super.setA(a);
		}
		@SuppressWarnings("unchecked")
		@Override
		public V5<A, B, C, D, E> setB(B b) {
			return (V5<A, B, C, D, E>)super.setB(b);
		}
		@SuppressWarnings("unchecked")
		@Override
		public V5<A, B, C, D, E> setC(C c) {
			return (V5<A, B, C, D, E>)super.setC(c);
		}
		@SuppressWarnings("unchecked")
		@Override
		public V5<A, B, C, D, E> setD(D d) {
			return (V5<A, B, C, D, E>)super.setD(d);
		}
		@Override
		public String toString() {
			return super.toString() + ";E="+e;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((e == null) ? 0 : e.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			V5<?,?,?,?,?> other = (V5<?,?,?,?,?>) obj;
			if (e == null) {
				if (other.e != null)
					return false;
			} else if (!e.equals(other.e))
				return false;
			return true;
		}
		@SuppressWarnings("unchecked")
		@Override
		public int compareTo(Object o) {
			if (o == null) return -1;
			V5<A,B,C,D,E> oa;
			if (!(o instanceof V5<?,?,?,?,?>)) {
				return toString().compareTo(o.toString());
			} else {
				oa = (V5<A,B,C,D,E>)o;
			}
			int comparePropA = compareProp(getA(), oa.getA());
			if (comparePropA != 0) return comparePropA;
			int comparePropB = compareProp(getB(), oa.getB());
			if (comparePropB != 0) return comparePropB;
			int comparePropC = compareProp(getC(), oa.getC());
			if (comparePropC != 0) return comparePropC;
			int comparePropD = compareProp(getD(), oa.getD());
			if (comparePropD != 0) return comparePropD;
			int comparePropE = compareProp(getE(), oa.getE());
			if (comparePropE != 0) return comparePropE;
			return 0;
		}
	}
}