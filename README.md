# ringbelt

Utility tool belt for [Ring](https://github.com/ring-clojure) web applications.

**Requires Clojure 1.7.0 or higher.**

_Early days, API may change across versions._


## Rationale

Ring is a great abstraction and a library, but integrating a web application with Ring takes a lot of plumbing
across various concerns. In many cases that plumbing is _ad hoc_ and dominates business logic. Ringbelt offers
utility functions to parse requests, generate responses and translate errors between web and non-web layers.


## Usage

Clojars coordinates: `[ringbelt "0.1.0"]`

See [documentation](doc/intro.md) for usage.


## License

Copyright © 2017 Shantanu Kumar (kumar.shantanu@gmail.com, shantanu.kumar@concur.com)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
