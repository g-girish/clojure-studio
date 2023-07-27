(ns protocol.waiter)

(defprotocol Waiter
    (details [this])
    (respondToCustomer [this])
    (takeOrder [this])
    (placeOrderToQueue [this])
    (handleOrderPrepareNotification [this])
    (serveDishToCustomer [this])
    (takeBillRequest [this])
    (acceptPayment [this])
    (payMoneyToCashier [this])