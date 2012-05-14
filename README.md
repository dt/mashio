Mashio - a Content-injecting Rdio Proxy
==
Mashio consists of 3 parts:
  * An proxy server which wraps the rdio API and injects custom content into their API responses
  * A web app to add, remove and manage custom content
  * An streaming server to stream transcoded content to the rdio player

API Proxy
==
The API proxy is build in Scala, using [finagle](https://github.com/twitter/finagle) and [play-json](https://github.com/tinystatemachine/play-json) to recieve, filter and manipulate API queries from the rdio appliation, and [Salat](https://github.com/novus/salat) for talking to mongo.

Web App
==
The webapp uses [Play Framework](http://www.playframework.org/documentation/2.0/Home), [Salat](https://github.com/novus/salat) and [Bootstrap](https://github.com/twitter/bootstrap).

Streaming
==
TODO

