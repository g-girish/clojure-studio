(ns protocol.chef)

(defprotocol Chef
    (prepareDish [this])
    (notifyOrderComplete [this]))
