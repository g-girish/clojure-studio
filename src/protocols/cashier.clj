(ns protocol.cashier)

(defprotocol Cashier
    (prepareBill [this])
    (acceptPayment [this]))
