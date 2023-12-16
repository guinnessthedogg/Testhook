
package com.xxx.zzz.aall.okhttp3ll;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nullableq;


public final class Challengeza {
  private final String scheme;
  private final String realm;

  public Challengeza(String scheme, String realm) {
    if (scheme == null) throw new NullPointerException("scheme == null");
    if (realm == null) throw new NullPointerException("realm == null");
    this.scheme = scheme;
    this.realm = realm;
  }

  
  public String scheme() {
    return scheme;
  }

  
  public String realm() {
    return realm;
  }

  @Override public boolean equals(@Nullableq Object other) {
    return other instanceof Challengeza
        && ((Challengeza) other).scheme.equals(scheme)
        && ((Challengeza) other).realm.equals(realm);
  }

  @Override public int hashCode() {
    int result = 29;
    result = 31 * result + realm.hashCode();
    result = 31 * result + scheme.hashCode();
    return result;
  }

  @Override public String toString() {
    return scheme + " realm=\"" + realm + "\"";
  }
}
