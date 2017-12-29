;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns ringlet.util
  (:require
    [stringer.core :as stringer])
  (:import
    [clojure.lang Named]))


(defn expected
  "Throw illegal input exception citing `expectation` and what was `found` did not match. Optionally accept a predicate
  fn to test `found` before throwing the exception."
  ([expectation found]
    (throw (IllegalArgumentException.
             (format "Expected %s, but found (%s) %s" expectation (class found) (pr-str found)))))
  ([pred expectation found]
    (when-not (pred found)
      (expected expectation found))))


(defn as-str
  "Turn anything into string."
  [x]
  (cond
    (instance? Named x) (let [right (name x)]
                          (if-let [left (namespace x)]
                            (stringer/strcat left \/ right)
                            right))
    (string? x)         x
    :otherwise          (stringer/strcat x)))


(defn clean-uuid
  "Generate or convert UUID into a sanitized, lower-case form."
  (^String []
   (clean-uuid (.toString (java.util.UUID/randomUUID))))
  (^String [^String uuid]
   (if (nil? uuid)
     nil
     (let [n (.length uuid)
           ^StringBuilder b (StringBuilder. n)]
       (loop [i 0]
         (if (>= i n)
           (.toString b)
           (let [c (.charAt uuid i)]
             (when (Character/isLetterOrDigit c) ; ignore non-letter and non-numeric
               ;; make lower-case before adding
               (.append b (Character/toLowerCase c)))
             (recur (unchecked-inc i)))))))))


;; ----- updating maps -----


(defn assoc-missing
  ([m k v]               (if (contains? m k) m (assoc m k v)))
  ([m k v k2 v2]         (-> m
                           (assoc-missing k v)
                           (assoc-missing k2 v2)))
  ([m k v k2 v2 & pairs] (reduce (fn [m [k v]] (assoc-missing m k v))
                           (-> m
                             (assoc-missing k v)
                             (assoc-missing k2 v2))
                           (partition 2 pairs))))


(defn assoc-present
  ([m k v]               (if (contains? m k) (assoc m k v) m))
  ([m k v k2 v2]         (-> m
                           (assoc-present k v)
                           (assoc-present k v k2 v2)))
  ([m k v k2 v2 & pairs] (reduce (fn [m [k v]] (assoc-present m k v))
                           (-> m
                             (assoc-present k v)
                             (assoc-present k2 v2))
                           (partition 2 pairs))))


(defn update-missing
  ([m k f]              (if (contains? m k) m (update m k f)))
  ([m k f x]            (if (contains? m k) m (update m k f x)))
  ([m k f x y]          (if (contains? m k) m (update m k f x y)))
  ([m k f x y z]        (if (contains? m k) m (update m k f x y z)))
  ([m k f x y z & more] (if (contains? m k) m (apply update m k f x y z more))))


(defn update-present
  ([m k f]              (if (contains? m k) (update m k f)                  m))
  ([m k f x]            (if (contains? m k) (update m k f x)                m))
  ([m k f x y]          (if (contains? m k) (update m k f x y)              m))
  ([m k f x y z]        (if (contains? m k) (update m k f x y z)            m))
  ([m k f x y z & more] (if (contains? m k) (apply update m k f x y z more) m)))


(defn contains-path?
  "Given a map and a key-path sequence return true if the path exists, false otherwise."
  [m ks]
  (loop [cm m        ; current map
         ks (seq ks)]
    (if ks
      (let [k (first ks)]
        (if (contains? cm k)
          (recur (get cm k) (next ks))
          false))
      true)))


(defn assoc-in-missing
  [m ks v]
  (if (contains-path? m ks)
    m
    (assoc-in m ks v)))


(defn assoc-in-present
  [m ks v]
  (if (contains-path? m ks)
    (assoc-in m ks v)
    m))


(defn update-in-missing
  [m ks f]
  (if (contains-path? m ks)
    m
    (update-in m ks f)))


(defn update-in-present
  [m ks f]
  (if (contains-path? m ks)
    (update-in m ks f)
    m))
