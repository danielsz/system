(ns system.monitoring.monitoring
  "Monitoring API. This namespace defines the `Monitoring' protocol,
  alongside a default implementation that can be used to query the
  systems about their status.

  The default implementation returns `:unknown'.")

(defprotocol Monitoring
  "Protocol defining a single function, `status', that different
  components can use to expose the current status."
  (status [c]))
