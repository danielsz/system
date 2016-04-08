(ns system.monitoring.core
  "Monitoring API. This namespace defines the `Monitoring' protocol 
  and extends it for components that have an API to query the status of 
  the service")

(defprotocol Monitoring
  "Protocol defining a single function, `status', that different
  components can use to expose the current status."
  (started? [component] "query if component is started?")
  (stopped? [component] "query if component is stopped?"))


