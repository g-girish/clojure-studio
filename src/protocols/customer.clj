(ns protocols.customer)

(defprotocol Customer
    (placeOrder [this]))
;    (requestBill [this])
;    (makePayment [this]))
