# livewire

This is a websocket Clojure app, broadcasting Postgres notifications to authenticated clients.

## Usage

~~~sh
    $ java -jar livewire-0.1.0-standalone.jar [args]
~~~

## Options

TODO: listing of options this app accepts.

## Examples

...

## Installation

This app relies on `leiningen` for dependencies and such.

- Clone this repository
- `lein run`

### TODO

- Authenticate via macro on connection acceptance
- Authenticate via macro on message broadcast

### Acknowledgements

- [Postgres listen/notify in Clojure][0] using http://impossibl.github.io/pgjdbc-ng/
- [Clojure websocket-shootout][1]

## License

[0]: https://gist.github.com/mikeball/ba04dd5479f51c00205f
[1]: https://github.com/hashrocket/websocket-shootout/tree/master/clojure/httpkit
