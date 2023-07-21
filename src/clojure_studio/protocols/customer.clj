(ns protocol.customer)

(defprotocol Employee
    (details [this])
    (selectDish [this])
    (callWaiter [this])
    (placeOrder [this])
    (requestBill [this])
    (makePayment [this]))
