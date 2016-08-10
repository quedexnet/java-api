package net.quedex.client.user;

public class Fixtures {
    private Fixtures() {}

    static final String LAST_NONCE_STR = "{\"id\":-1,\"type\":\"data\",\"data\":\"-----BEGIN PGP MESSAGE-----\\nVersion: QPG\\n\\nhQEMA7beSC/wYypkAQf6AmB4TJpsuanOJYonjMbo7UUlZ7XNJZqeTQH4CsrvlopY\\nnJ/ZgJdplnngJfZdEeEde6ccD7Wj/xVju84p1t/JyVQpn4iB69I5H796CVeE35mW\\nIAVOdWKjxVDnlbYuoNjWzb11laAKIcnIQhMc5z+8FHH1CsEXJrAulqyL72ExVRpx\\nC9X8cEsWQ0TNnehv4Yv3/cnz0wH7PICP5LvRn7M8d6JRS7lYsoHXYJgRe6MSf+cw\\nilx+8Cjrf1NrocakW6kPcCklvQ6Z3qbIdKHbYcxBzqSZ44L83AC8VIxLx5ESKVb8\\nqGJbD+cjsJofRQKjaSMWWdqCUctrJyGgApYLj4wFItLoAZn99iw4Yp9N9OTiXo6J\\nrZa8IKQVYMGdN0L6i9B2qzknkJM++6+cbZRAisYBW2nbxJ7ZRLxm7/ojfwv31LKL\\nFF5BP3/xmEiTht969qmEBxFMjkqq8P0MXbjwwuklFnbfnW2skuhF9J4eQMwDwj8D\\n87YN2px9qVImwypnyNT/EQfkGb4XaJWvryJVRKq/l9eH8ZnEm3KH92E+piZad+TR\\nqUKYMnhaL6LcUHj9VA68YuNvj7RY/kb9ttAbZpsWnpkhfLty95ltbSemHX4zKOyp\\nxq34faS0qio79FHm2nsUZmWsIvjMyNzDdUZZpM3Hgqea4UuAagEw0mdib2UVS5ix\\nccAGSr1DhpU3FqPDwn4kYpIBSGAwr8M5ZNwo0TDjPGBrlG81ydYF4qG49kqnWA2D\\nH9RzEajIXBbx3KyynV/utBKRjZjrEuXObKN4M5GYkoR0lx6GZtgxHMOMJaAxCyhF\\nxHkzHxAxgc3aWlKKz7iYga0eY24aJ8oPruGZj9MBELp7KwPbFCxE/aO9gVtmFDuV\\nYYccVkvQX+ArVpyRzUsOp7udgtrYDSSQUmFSeHkWyv68Zfm1H2ISvSNp4gbwIt+Y\\nf6DEJKWmWqOw\\n=Yk28\\n-----END PGP MESSAGE-----\\n\"}";

    static final String ACCOUNT_STATE_STR = "{\"id\":3,\"type\":\"data\",\"data\":\"-----BEGIN PGP MESSAGE-----\\nVersion: QPG\\n\\nhQEMA7beSC/wYypkAQf+IPSe6cR0HFoGk6cv3OHUFO5W95RG9FFWTi9/plxakVls\\nEY1lnsQgTv1h7WtCC5FY+/k3U9WRiznMormvAGoApbVoOqid7SZdDEf2lZhr62i/\\n6Jk9fg1d4QAd3aIGfptsIDG7hVvOb9k9yHBZDa+viBAMVXiETW0vfRnlKW8oXAMa\\nw5VZ6t4/z3vZPc+vmNre4Hf9t8J4kqNDYQKsjdI/Pup6lN2Am2mlPnXnw0w9rnqj\\nNwwzr1Vpql2MAVDPFm904UWtZvYbxfhL9JcgQzGghgynT7rHiehCDayhfRqG1fn1\\nxYgOZycm3tKf6zvPiYIMinPuXSpCQiqW1tNbpVY+0tLoAZdgrRKA4kF23zTw/suf\\nRdqOBcC2EP7D97EY5immQBVPLpUlVAq8UkWoVXXzQEI0X69x0mQU+Te22wC0Cuft\\ns8S7mIYxP7si0nAdShNsG4GJpbPhREkwNF88urtd/gK63WP2Tp2Gea62DRYORaq2\\n/fIE0Rpd1lsRXUcWRphvDwb8pdtYAB81C1j2Ua9TV8ZovgQnamXSNGvhrx7k5JeC\\nFD6T8rhizkVfUrLWW5CXs24X8ftoKwSn13sZq511Lnv5SoVedmfY14Id20J4jpdb\\nIdFhBgG13hzONtyHpezDm8zpgWRFMxRkI+evteimnge1FUaV920wNCr65uw9/R9I\\nk+gO/ed8y7pRNAwW9sAHrkt5O2BJjQW5DVVi1KTC50ST3TkRhHPVG5TMwZirXRm9\\nBLvwPAV4d26cibPTOdSBFz7xW35yD6Sn6TXWySKfiVhyFiuP+kDJ7orHBGOTXqJc\\nzoTeq1nTJ5wynx8bAVW+4Qsw+cE0Ql9OHJpSt2X+sS4czUfcFMmCNZ8iy8DFYnpe\\nwN4mHwxXft0jWvE9/WRyEZdj8yzEgegWQO4nUjuPLAuvAdjgKAepjs8J/vgH6Sl8\\nl3cjArQtV65nB+h274tQm2npP+65K527XaMqUqelRvSnN7biOMG+bKRmE7oiuc1Z\\nO/24U1Klujh9WK5po2Oc3c/aVgrjkmIhddsbQGs+EtbBy+3Ruc1ZJxBu965QPFhb\\nnFyZN1W6lqlg3eXIhMfpK3HJwjf52PJp14n3p8OZZzbp8lSRsSTvPHhFN3e2NWIA\\nyN+KNuy06vcs\\n=g6Za\\n-----END PGP MESSAGE-----\\n\"}";

    static final String ORDER_MODIFICATION_FAILED_STR = "{\"id\":5,\"type\":\"data\",\"data\":\"-----BEGIN PGP MESSAGE-----\\nVersion: QPG\\n\\nhQEMA7beSC/wYypkAQf/QO9fkG3sSoCFAC6rtUTJRrKUn2koFy6x5bvvPhQ002d0\\ncOBGZ7GP29suBSYvb8W2P7/tRalnphe3jYzobPKEjlxNfEla3UVlf0KoBeSq6KzJ\\naWpe7+h7kNHaHxGIDYUB8z7nCBNpRcqarWevd8K7lAhqg3qS5qDltFkiPJDgHADt\\n3kf1suJjguTV0lkqJdhKS8yDg5V3GKXuENA+2fwX8z0c9vFfht5fdh41Z/91hTzA\\nfzyuQDF/G3YLmkJxn4rQ4ORMnJdpQSYC1T3F0JPuQUEAYdXb8Z9fxrhCuyfhLbgO\\nBV553UU09d7G1EVEAnOVemqtZ5E22JuoMROUU7kdftLoAXJVUqhqchwrb41/jyp2\\ndSo1GcdzEOi+nQq+6roGGxotOzijYTR73+RF+BtVZnp7aAz1lXY1P94bTZeSfCVU\\nkZi5dQa2RF8sdPyku5iOKQfUto/JXRhF3GJ5sOMxITIavGV31isjxTKI0l/51u/O\\nLkL7tfxHHyo+PpabGcfiHsxiBXt9OM2r1Y2f4eNEwsEgNDPvF/FbjhjQe/Pb0Ekt\\nmv532IIy5JBqhYEGuIag2vN0TOSFScIjiGwnILB/8mJT5T2/jlL8QXQtP+TdaW9i\\nmCVJQYfa0J62luptL1S0rfwCp4JtRyj7XjTgW3O0VMGOHsEF85/GBOe/99Zeh/Du\\njuhtxAk5mI6o8Ul4A4K6qx69Adp8SrtMz34ipNJ/E92IzWU/JXRRvfFneBReU3sC\\nnrSqtsvQcBiX2Wgn5OEHWTEjrN3aAxt6ISh/fsaRSQXQOF37OKenqXj3gBmviHzZ\\ngVVjRH/orxS2mirbypDKzQxY7QaYDOewY++C5I6irZRbH9XKQlwQlQF87FRJHtqv\\nfXti0XxjKvvbJ7TjoKKBhQaIHVu4MHsSuminzfSsRezweXGL5eDdtw482IM3JhGB\\nqhr1JdhZ6Vq/IS0TY2fFwZZPOAvYM0XAJt+7cvrSF7Ah5txQQA9vAfv50vp3kMv9\\nVoiD6gXNc15B2RxfUAF9aKr+BCHsm1w=\\n=CRU3\\n-----END PGP MESSAGE-----\\n\"}";

    static final String ORDER_PLACE_FAILED_STR = "{\"id\":5,\"type\":\"data\",\"data\":\"-----BEGIN PGP MESSAGE-----\\nVersion: QPG\\n\\nhQEMA7beSC/wYypkAQf9EVqiUyhP3z8k3DBjZPobBI9R93XKWPMa752PyvhWVp4Q\\nGqPby33VREhQwTMuPNiWqVKtAeZGG8Mw/5HyKALAzsSw2rGElmIZVGa0YBa6UrJN\\n/drAHaM4zMXoUit91dvOPT/Q866xW5ttEVkXCzpybkf+aN6AqtvGAR1CvvyOZMU4\\ngNKvix2ZTKMjSWVg7cgCkfvJ0kbGNkqzFqHYhxuo6tQoTRz9DH3xP9Q4BgzRLSj2\\nyOsJLvVm0ZBSZ/GFy5blRmj7soiAMDMTP1722/9UPR7rIUulosAE9ZJn5eOwSxRK\\nCtwc8p2xRihud9taCyQFxH+E5r6F3LPi5hzD5sOrstLoAQcUaGwLMO+Fx2F5pqCF\\nIX/1fctsS+sfyOaVacXxYcul7Z94ZKCV4vNzevIMBbTivEobF4opnZIHqUPrpaZf\\n+JND7lj9ookY2yHdZlRiGtuWuudkg4eTrjGODhkuDjquX0rEq7W3727nBHQq15FX\\nxKtdKYjRPhSvEYXMNkj4tL11gClygI4luhENjo5VpC3P9ZMQ1sJdVvE5GHH3gjzi\\nxjt6TDDeLzzNQfbCuNy6wy6RojFPJJKFwllOxawE4NBOqerpxFelJ/oG/MPKNaHQ\\nB1IeSOeTB+SnfRPIuTKQusz8c60ap/AXg92ItaRl0jyKsM82kJhi9zZJy/zX1zL4\\n+eiD+LJoUw3Jx0VyBSrAcny8pn8Fz69CMkVDDJEPX+DXrvd0R/tTRpvSTnJfyhAD\\nOhQVgO79//CfYYrypzL+znQc0bikb9B7xd2O0o9S75mzVVHZ9QP+xcLHKG47VC9n\\ni3v3XFkv8Ak3XA6iRPaj1tXmGAfAYYNXjIPgannkShRUQoJv3ZyQjV6ey0xrtHnm\\nE7EvjUnMPcufUAdxRRxlmBR5VxywJH4hK3a2eCc/B6V1B58s/gX+qCmTMaQ9mqkD\\n5MHZrxSWJc0iAyevIVmD/MMVHU3mCVqWVJUQaMBZpZAAED9CLaZrM6kP47f1gCz/\\neBsbJrpl9zvS3Kk5n8QzWpDXDngLv1f0P+Mj89I89yFc\\n=hx1m\\n-----END PGP MESSAGE-----\\n\"}";

    static final String ORDER_PLACED_STR = "{\"id\":5,\"type\":\"data\",\"data\":\"-----BEGIN PGP MESSAGE-----\\nVersion: QPG\\n\\nhQEMA7beSC/wYypkAQf/XCQFAmTU38gwekqL5XlRoHQUTY/bbSEyfMQfJIhC01WM\\nl8OPnKucwNPFkthQsb/RW4m/RdbsPY156PA68KYTTN3PYIO+T2eUEXLy/RNxdh9w\\nTY3nC/aPuXWe2sIOImmj3HHYeiL29EZtQ+kT34M3h/WeaPNKxOxlIS52PfVFL2yH\\n9aqNzqoUEIeSLBQQUC9IxBQVONVoFkPHqlNBnKio4qFEqp1nvONfdLIV8fIbuhNB\\nniY8cJXb4ALYXJNMOYOiFxXFk+ENLE6DtfO3QZiv6X40NDQYVluWoG5pYmKEV7uV\\nhl0o3N663iUbf8AxAooJ3PlMs60vSc2oRNPBSMt8n9LoATFQ1BeeOJfF3PeUsFNO\\nUXoP9OXUvCmfhGvxrqXc8AYF0X/qyFXPk6kM/Ggwiz1bLbS2r/9uwDwj/JaT5mdS\\nwHRZXo+DgXTqW9RMkMKn+DkcBDRtedZPn+Hd6N5qICxstZXqaDPAHp0I0qQ4hq21\\n3ZZZYNeHs3mqXLw+QDFjLfEdZRkjI3FD7l3q1Q8ECSROcziWwMKcvhxfxPwgEOo/\\n6vKIhzDAXy+k/8u0MKp4wxLZT4S8eRQOJskkCFOxf+PuuY63a/lHum/401KsyGTs\\nF57CpGrqIAJi6yz6Pf0c2hnL7B8h9+UDds7XKknv/4hzH7LXk6+J23gDdCbvv2Dt\\nh+gGIpyg7KtG4gmRNiumWetkEObNUopkqfZSxiWX6vllZe3H/uD48s+5qcIzmDV/\\nkDU0Pw3FJRQqpJE7Gv5HnSYAMXYF5XKl5gPvxnsYUX6xVUnqKzLc4Efkz4FxfBX8\\nIfosUrtXG6i7i9IymW4PyKGNCOcMqHASObWv3PgLNzQyIx5xfdOk5dnmghAJ53YE\\nt/M9V23NDlDf52z22W/pKsb+6rcXoZkArFhvvQDXX/egrcTNhVAlxQDrv95N+EoH\\nPr0Js6ZiGpZvlLi9JKlYPQm4IHSf3idyPyivCvJ1CX4ju5TkvMqqHu9M6VGR231y\\ngf3qtAcyzCUK+QBhA04vApBwNMVzLWHin2UiTx9KFSslY95cDUzVV7F84kG70Rto\\nFPSgwH79WoCrLQYrtyCzVDE9KSknbWk=\\n=6unJ\\n-----END PGP MESSAGE-----\\n\"}";

    static final String ORDER_MODIFIED_STR = "{\"id\":19,\"type\":\"data\",\"data\":\"-----BEGIN PGP MESSAGE-----\\nVersion: QPG\\n\\nhQEMA7beSC/wYypkAQf/SaHByZT77f8AosvfuDRTrdtOhl5HjCEjetzd6wfk5Ad/\\nZ6RWI8J5EyjsSCRV5Sr7UAwJ80kLZmosCGCX12+LP9RtD+6pngyU/BEUfYsNBcRY\\nO6cJ1u4KQHIzgD9CXDtoTZU5p3qb9VdJokCY87DGiA75gwNuwdC4/7THQJ5b3eNF\\nsyLMLoQ70EvJdUXWFrnxfAHBIK5Pz3XGSTpZlT/2fZuIZ7gPSZLsfiX8PWYRcXVu\\ndOeqtI7NTXAJy9oCN/REOtH2PI4DKP5RszwpXagOVzB4QVQE1dMWQ9+N4nKgSRfd\\nwpLZtvzctRuWXM4gPLbb05/Mpqy4sslbsuF8GdEAbtLoAZ/l6daGPfJZbAHi2RLz\\noS/NWjC5AOrbBwBm0GNaqa6zCFEAYDnj95KB8Qhr45FvsgaD5sJNFOqejzzZ6wqW\\nqzUK4E9ByRZy+ZryB55S+WH/u3bTCGwSYzE9+n/1Z+fvE/dq8/a9pz3nFzQioq1Q\\n3LAQbqyOuYOViKG54ErU1dTskMsidLumTAAa7DdFtOCN1iwOoV483fTv3S9QP20g\\n0o3+GkACld4uYsULdE1jjpXiu+bDJ7qiy+gt2XztrYVRIV4yfwydf4dhqAnrVyhA\\n6iYWzcHnjQrmKXQBQYdVSp16QlSsWi6OtW2X5DxugBHlXTcJswczH8G6qL3rjOy1\\neMAwCZhIBwaELbASydCADTXY92xOtYiI7UYmkhk78k4fPy8vHfuoXsqq1xuuKrrd\\n0alE83vHT2RH6m5/yIj7EAav0hQh2lXgHVWo/hNGB+BJXJfbveup5bFqRwSQc2Fe\\n6btTwDa+A7qIjVzuR9cGbrFFZbINIXU5wL/ejzqYFPLCcOm0D/H7TasILHRA+U3c\\nhUg4Ol8L/Lc4sjy0bkbbxdL+Ss7T17xn1/wZBO4eWQV+Vj48xuhL9VOq2UKwpWzZ\\nPmKl701YkbKr7YebULfbkgOAFgF1Zdji5wmCae7jAcYqLfB5CoEfE7Re1chhRuzl\\n0xWd\\n=ZVt1\\n-----END PGP MESSAGE-----\\n\"}";

    static final String ORDER_CANCELED_STR = "{\"id\":30,\"type\":\"data\",\"data\":\"-----BEGIN PGP MESSAGE-----\\nVersion: QPG\\n\\nhQEMA7beSC/wYypkAQf/SlNpeak2xmNYTM+1/7r7l/uSAEPg3cQ8F3Am1CaCY2lA\\nE0myA90E3fjTkMJuubQntIy0ZfhJFRAjcQob1M8/rqhKztT1Wn+r5R9xxyfI0YmA\\nRSLChRXiLF3RvScK2X3E9e34bovrg+8bvKGVKyTSR9MAZS/x7QZzpHhfBpJ2+fEn\\nar9AY86hJvT769xkoDmd+I+Rl7zwk0qaeI02oQUQfOVszNITfcIQNMWlQ8T9PlJz\\nzMEd5Ct3hQzq7yjFv3Sc2vGS1z+5kIRVdwIYY9kzSIlaWYehftCnT+KF6DjT5cez\\nEKzyhQUEA+rSZCBwzsJ+rtWuDipSGGcLjbKP2EdGH9LoAW9l+fVv6o4BDKN13pX0\\nIYFijUFNLX6l14S1XElnl54EFHknbDZ/cXC21ZUHx1IMwtJxyQZSyRDBSBF41cVk\\naP00Qj2vFVJ/vKLm1XsBazFn9p4OwOJhaLl2ryF8ql9LXJOtcUwh8WdzhT0d0grW\\nCAwQRpwDHxKLeMId+BpZwovcmhYamORdfZ2Lnq3lrLXSUKmbIJ1h/xp+hhY1QNpg\\nuCe77uBIHemcCBJK9xVyGW05h62lB7F/Vzi4zfxvuS6p/A/bjpbm/e/wO/wm3nv2\\n2WFJ0dehg8mJq9FP17hB4m4Mt5amXeKzagIFl9MK4655xC63B1bCumgtgrDYJ+DW\\nvMAxZd73loAjH34oVNuZ60NlvBzVFGE0Sojq5VCrPM0HN4pswDPxsU/ARuJr5JCP\\nmAf9XvifH7k3dxLsos2mmWXEFpDIsnhoPve8ZUtQ1V9kwgWCqY0CkqgHJKnC1KIf\\ngMBkQi8vBZVDMoWdTgnSpzvJFBTvzb0f8fxAvx+mfKiDT3bIEWcmSB8/4r1UPOFZ\\nmCW6RY1b9EJ5jZ8eQJcyDXnof3/NGlVdfRRkSDp8ScifCmA+o+3PQIQKGaIU+rUl\\nfrRsVFfr2hchVk5yEYpMdvQqXECR/ifWRvwMLNm28YJ3xVlhWwV5EnGk9PUFy3tp\\nsUSssA==\\n=42Sv\\n-----END PGP MESSAGE-----\\n\"}";

    static final String ORDER_CANCEL_FAILED_STR = "{\"id\":9,\"type\":\"data\",\"data\":\"-----BEGIN PGP MESSAGE-----\\nVersion: QPG\\n\\nhQEMA7beSC/wYypkAQf+I0flAKhOPqSxNWHcHiOlV3m7E05SNp/tOPdKQByBmfmD\\nW2+7dlF5r1Z79qprukJUWH+b6uy8YCyiLQ3NO0ZPy3yEG1DpMYiqxZUNVvjTfNYD\\nmOMbbq7mNcbPRjOJfbNZzRTrK7Cqmc/mA/RuVdLudn6LtlR2Kk4rj+/V2du+Nsly\\niCHE1wLCqUul2PXaFc/k6POGT++hIfw1ZlqTDdp+WiEBKpzLeIvEjqWEOee+l7RW\\nwooDR3AMe8K86B4pONgwoOHbawPS9oNQwBSHyES/y1D54f0pv+qNdajWlp+AL2jH\\n11LgrGO7hnB56lgU2/6IYy+7ibOYx36WA1OZGHQFDdLoAYGr8gZlkAJUKgH8MdPS\\nT9sXQtbNcnsQUHB+1Ai4rpIYSAjM7wPmh8cdggzN4F2hNsfu9+uAcKZry+bbFq47\\n4F9q9hK6QJHbQ76bDsnxGgXBXk/ZLug1KjPVHxmVY3CDdpLCxjhG2iRDLtF8R117\\nk2D8SNQcHuksjvIf8ymWgjew1K/XMti+c3payQgT3jpcszHdQInGSNaelM6lprSB\\n5lUTU4kP6g02Q9xzjUcG/Xr1opmbPwwavN1kUn3y/2IgFRcqvWQqwrjxUhEGhoTB\\ncUP/z17w85by8SMmfQ8/u6/tOQeZyp9vbehFgnMTPKkY5kCBV9LF1Gy5hwYDyofR\\nncA+hS0UK36dJtQSUfohMyhWXxONqGEh7wpiCg9aiOht2wcU0bOZJdgt4lDt5dmW\\nwleIbArIohWnXxKQdrnlpEdVOysyPlxnz3odOEUx76iaMhei7Mq0gJpv5qbqMP73\\nni4u4pRBjVmA2lonTW/h7WrFdTTjqr+FDSxSd5Pa+qgR3iuoV2d4i41Vw3OXagQn\\nrSaR9IzJF07vQypJqcfNrE3FXOR9MwFNf725t5ZjoyA+Nk9T++ss7X8Fg3UQNMzs\\nPJmTXPLZ4ZcWLhG7aMNKU7+L07+7LPnlSBTMzKyQKsEu8lPT1iu7HWzivqZ+be1/\\nggV2uzKO7e1MhJ7d/Z9iauk=\\n=FNjx\\n-----END PGP MESSAGE-----\\n\"}";

    static final String ORDER_FILLED_STR = "{\"id\":10,\"type\":\"data\",\"data\":\"-----BEGIN PGP MESSAGE-----\\nVersion: QPG\\n\\nhQEMA7beSC/wYypkAQgAw5Trc8DJTIBuOXdx+ujRdDx7Zgc9QQ17ivzZNYGSEWsz\\nLKZDlXNbp5iSU6eTjFtLofdBzhqTHHaiUhC2VmWLKjYQEzEMZVCZvjhZQGEliN+0\\nmLNo7CdA5tcC1WFk0fcszrUMH3Vinv4CLmIoPNHHEdbm0aCGYoU48A3E7zHDC94T\\n4fA8ux3ftgqUYomajv/R0o5TGRm9/EUTGdf/6SPYXS7OddzTljvuHs1nAViPOYMG\\nJiUNTlpbLUoAylHQsqgdXnp9BQkVGJmdDxw/c6fp79GiE6jEZhLvW942GD59ZwSY\\nFoWa5ftDHkmPMZcMMGZOYrNOuggPnXDlU8YFe2AGhdLoAUdu2AVlWwXnCykGo6Y4\\nl1zp0FwkjbcVOTdyb9sd3sV4LmOWTcac5GW7uO92fxSUX/q3Hzb2opCaIhVmB4So\\nhvx/302X/c8LLt1NloUppT1siFO34fA5wB5xvRFkzzBb4Biu8yqgpuCPBpqUnv9O\\nwSipdFIoQSFqBR2PZGs2WxZtgJCX6aejeYc/eVfl4mHaXYuOIPvieLq1ciT+hmgv\\nfu/oF6qgek2PdtfaP+IW598SmLuBybD2L+/A7ZsqF/I8CxSdVpm9WtxCFpjuEsPb\\n8ppa4PUCXX5ppOYJqWdlge6bow8P8zxBIrFXkTCbiAL26jo5rIkx0vSaEmmmkJ+H\\nacAhFvCBdsTK4rD1MbixjDDArAdF8Cd1w+jad/AZ27f026nsGkEIaoigd7q0A5k5\\npUSWM5jTGMhHUQEXbeJKnYWS6DXgqVlpCzqFYfCVO4z/RVdJgm8YhmlhAoPwXfuy\\niyuUeKbotRCu7C4Ben7zbfKmpQpKR5m0Q2Vd+fSDZkpDrtgLIiptsZNUk4nd0KxC\\nLhFamdL3Hcg77lOFQpTpnLvRMt516ARYcBlsY1SpnndupkfeMaR03rcTnAwnRFKW\\n5YB07+H2yNMM03TeYpzoLOLW26oRQFa77wCIkQtnb6H70bKa\\n=6hPF\\n-----END PGP MESSAGE-----\\n\"}";

    static final String OPEN_POSITION_STR = "{\"id\":6,\"type\":\"data\",\"data\":\"-----BEGIN PGP MESSAGE-----\\nVersion: QPG\\n\\nhQEMA7beSC/wYypkAQgAktdf94FUkI7ouqirUkJZKamRDhbOKwH2oL98+QUQe977\\nlTXIYsmugCowuqFIIbF6f9o5p4XMTNIHHNaeCKc99woqtKDbm2F/gIrkBc16/J1r\\nub860G9dH3HsbisdYdOrP6qGdvlx8pe0uo2yohlHedhsxG8YYr/lxYXFtM63w13c\\nj/YEfjrAvAaeAxMybI6DomodrXXCX+NquXJbV6dwSGWynvp+nzYdfLS9M/O4sQFq\\nAv/N5YXpsxxkGEGbui0w7bDx382mckz0jZOumNr5aVhXJ4YtncuB644zYywrZey0\\nBw7LfwrN/7rZV31Xt5g8LsU/LIiyQ/JAiuaxbpI9KdLoAcobBzAJXqlePfDl/7WS\\nktYvcw9+edXDwO4/bMJmovKt9phNRXqZLKcVU430QS+acA7PmYStZWWiliO2E21z\\n8ef86ErFIe0FVP52INK/oNl37llonu66i/iSnzJIXUXanyzl378MgkLPP7akkYdh\\nNbVLM16jyxOWJNAFRRPWgb/xI92E9uyFVCu2TK2CzMk9EhE+o045/dqstslXT7mJ\\ndA6UUSIdis3TLMtUpmEksteSGKcLmGaHslV82tvnjuYyRkVoM7m56LI/2FvBX5Er\\nxEUIMcx7CbRy6DTxR2gCXa+LU1Ec25HTmh1huvqw7KmQNG8uJoGAHWunleCFTY7B\\nHujmYGmecS7Y/iJ3xCI2XrEW+eM4Kq3vXKcxx3HJtbcHBh0vbXY59EegvgNnqmO9\\nV4OQFedr+LxOrH+wk/c0qutaO4BE+2Y2C7QMVmL6jDJUUSP4t9I+LjpmvIHhqfgL\\nsFSl1R3OSj9rO/FPh/rgPLOgjiyjojf85jIVLjktdCikevoXhdmIfT0nxb/gCsyO\\n6p4MiVyqZfMyQCEZimyc/OmzZfKN/JFrbLdS6jbM9lKZMJ3GTR7YYpU5pgJ3Cam3\\nmtgyy0rKNru0TV31U9oRhxhhtqHiPoMv/0LxMmN151QXvn6hy/cpLeJTDKp3KAKG\\n6lYeG4Vi8ZEjBCTujCjqWEpaOFKoFIZ7z28vgkGcFuQYZDupdpFlse0GYRFQaaTG\\nWOEWLAmQocYEJWRbS+ZfQTrFhSkvMECiUFF+\\n=2Rlc\\n-----END PGP MESSAGE-----\\n\"}";

    static final String PUB_KEY = "-----BEGIN PGP PUBLIC KEY BLOCK-----\n" +
            "    Version: GnuPG v1\n" +
            "    \n" +
            "    mQENBFVCsVABCACweQ8WHklVrS5tlL0EPiRrUGkjz1y5zSR6tf5Jjp1kKl8d5dKt\n" +
            "    Yp61Qq0d7B2Frsih/nqkMqDt1us3T7jXZ/SHgkLrhiWrUGaD4HUK0Ki+/qDYM5k5\n" +
            "    kfyLfM+Kk6+XlxWl9VecJseeuOj7pEbepLJ39vg2Cn3gjG9o/H7y7u24VCOCiu2G\n" +
            "    d13HXwcLOUifjHAtXwngPNAGCxfqO+I93cG73A2Qx6/cYpxkgXh9GauBja0Iszpv\n" +
            "    xnEaHwJfxNWjgujIeZMf9iFKkMSMwtCEYitVMMJtLS7UDy9BHf4TWLMaKqQ+7Iy/\n" +
            "    wPuO1mGl2/GEhWT0fHABBUNzvojtstasTJGtABEBAAG0JlF1ZWRleCBUZXN0IEtl\n" +
            "    eSAxIDxjb250YWN0QHF1ZWRleC5uZXQ+iQE3BBMBCAAhBQJVQrFQAhsDBQsJCAcC\n" +
            "    BhUICQoLAgQWAgMBAh4BAheAAAoJEO69Fx5tJ0UYHx8H/0MZhtUWRCuBr9os7Lfj\n" +
            "    NYEQ27ghL4CXIGu6oFjyLIbVFqj/EYfPfWgWIwNXPJL9zQf1wFRZyQl2qWzpez9u\n" +
            "    rr5AKSw5QlbAFXxA+l8u0W+IpLOaBJjfT2Yh9vSxlgsjDuSseK69u4RZIru3ZaTh\n" +
            "    myJFhu/4pxZ1AnpU3xa0WDZ/4Dgnfz2V9Bikf39bf4y8nZafD5p/fj2w60S+rW1U\n" +
            "    wK9BuMtmeyw0vFlapJk1gzEICq+ZzQLgG6iqL30APDJV05AgUFtOjzH7uBTday9r\n" +
            "    Nqt8tIhBXA/R6XPv2jLGPs9UE0HhyP+gICo1gXGp+X/Lew8tr44A/+RuubEdSBFp\n" +
            "    ajy5AQ0EVUKxUAEIAMpnXJctj9HotSo2YoncAQhSktVENdkIqhAqHZ9VxWjEQXil\n" +
            "    crAiBv4EKrLPiPCz1bg7iaz2Mte9M43df5c8texV+2rdM7VbJo0Rz54CxIoT2opw\n" +
            "    wxcam1yK/rou2YQIyl7nc3MEgf96AngBwkmK2YNtkCJvXxQ6cAWvJ82GwE2NHQ5g\n" +
            "    iutUlIlF5wd8qY7Fa0GLBR96qNE1KoJN1wvY362+HkbjJWce09EFw/veikBdIMep\n" +
            "    UZWn1s6D1A9hBEC4TL+p6l+c/+G0QsWDJuB7LDZW9MEtjmfg9svaxbeaH2RNygqg\n" +
            "    CyMaRaCMpwZIPWboEPYVe23q+IMuGBLz34LfhHkAEQEAAYkBHwQYAQgACQUCVUKx\n" +
            "    UAIbDAAKCRDuvRcebSdFGDyMCACoDCVDit8dS1zEajf7bUMzNmGVpQnv63YGuGsi\n" +
            "    9Y4+PWcPxRC8WYzfqqDyS5glxVNswqwf1RgD2E7/TYq2HnxFUvPTcwTtPUFCEr/r\n" +
            "    gCMjYp7BL2z8bVAzO/Ie3lir7xSYfg7UHd6gQipjDlBOAzGiYGQ6j+QbFsEz8H2C\n" +
            "    bb2y4uNtEVlElbJLwXt9Ui15w/4j62MADO8cRPiijF4X2p4GwpN8SgRnguz3cOaj\n" +
            "    xUURDVn8kiv0PBd/8Y5OvSUdMPDYTlOlYthm5VUz/8hcxf2YItzsnHcBfIs0aog7\n" +
            "    VPUQE6TH6Lni2qEjeCD8WGOofbyJ+NpAsRQpD43uUm/nNNFj\n" +
            "    =syaj\n" +
            "    -----END PGP PUBLIC KEY BLOCK-----";

    static final String PRV_KEY = "-----BEGIN PGP PRIVATE KEY BLOCK-----\n" +
            "Version: OpenPGP.js v2.2.2\n" +
            "Comment: http://openpgpjs.org\n\n" +
            "xcMGBFeq9ooBCADBy1d90437+Q4Q1s8p07Sv88ubrxmHjiqTQvh6cMIfaTHm\n" +
            "EcWBAFVhnPX7Wzmc2aMc7E2wj6EU7HMOoZPyNMSwHRveJ3EyN3bKsqQHpIlT\n" +
            "s0aE8nTtYS3WEetetz4+mhX7FpeoQQI5nVZHy0kZkzXFZbuwXfVC3hybDWdu\n" +
            "ErY7o1mzbiUxHPXmn/FScwrt7TevZ3QXuIzp4+Pd+53wG4ycbaBkE10MEEdF\n" +
            "aIblqu975+xsC6Ge/ddH84ZEX2FjoANupn86CVOmQlJzoHOKr1pFEyD0NwxG\n" +
            "2iLyYj2Ei8dNFbAbbmTvIdXbBwHeVa7GsT99CabWwrxVdIhnKTBFeTrHABEB\n" +
            "AAH+CQMIRwDcZbQbngJgrHpyByTrD3F44C1fynQges/CA9jlE88Rr754mo/N\n" +
            "ngmF6C6DIq5VTGtguTBHXXHQVNLSXwpzwNN76LVA0VqAlJinfHU2obJD5HEL\n" +
            "NP+phneubA0vnpucgCSduQYynUPEhFs4wbnYfxrMdM3guio8mBEhE8jU1YPx\n" +
            "9OdIDpztjRgIf1y+duE33DAc7ii/FoJbB+Uqv4nirHb5hQ0yBwyuq7qJ76ew\n" +
            "2a0d4RCoHhNgcPKUiRUVhce19/pE65JiWV1KZFKbUjCW5EfbYBuzxUdz0z1b\n" +
            "hS+mX8vucExflhsQ+6pzmlZoWdwJB3FJp9dKxo6vpeG4Lx33t1ZjLQwoCkd6\n" +
            "uDus+kOnHjLbftyyS+QESlvUKgSIAznMzzH2Vv4mTSnJRH0Yw6FvKX9PypQi\n" +
            "EhwSw8nK8Q9uOax4O4HzeeN1bmyOwdyMM9KiETACTnHgzip61XGwNZFWCBsN\n" +
            "wRU4Gy3LPTptr1qH8kWx7ra2ne7SRMtwa+yO9t7+5GeY7BvRJV3iprpPXQO2\n" +
            "sSnvVOs5u8DadaQBxcP0Uvs1w8g29CzfPtMT5DzmeTINS0lZQv6uyfgaaOPL\n" +
            "SQSkOuBn7EyhgoZnX8WAIWF7AAX8uD1L7hmnfruHkJ6bhlH78E8u5k5dVIND\n" +
            "ORD34Ex+Y4FIrch8dzbel1wlmCJMK1D7Cb48kflfyhe7OfZGwfEqr4UDaZwS\n" +
            "Me/djcDqPb4SsP6rtxfycw7u7cl3yc2TZHxT9meuKMiE9Nza9zhzgwzRp2jS\n" +
            "y/1SnqdHPNK24crujK6JAUudUPPQyg7G+CQG6Xp3OmqIPWg0xXnJNXh6AaYP\n" +
            "f5zMVr3bxLHd6/p78xbFpsuH032lQffvmnqkFM/prFR/1Z5+K/IVsuwx0llD\n" +
            "usxI8vkLQqbn8qZIGt4xbDWAcLqYdsK/zREgPHF3ZXJndGdAZ2VyLmdlPsLA\n" +
            "dQQQAQgAKQUCV6r2igYLCQcIAwIJEJaV4PGkqFVJBBUIAgoDFgIBAhkBAhsD\n" +
            "Ah4BAAA/jQgAwELWRs6LitsnD2gJjIfV55yitS6oi8VNkYz88ybCu0i3xbkY\n" +
            "wGc7OeNBJDHihnIYh6JMp48kRDhIW5yb7SCKGzu3vmq/fcnfXEJRvtejbPMu\n" +
            "RxVY4gZATmhwCx/0tvKrlog8k0QOfDHnI48+cvVGAoH1UyX81u7CWQDdVKZQ\n" +
            "u6R0Bk6lBCKcXA+/AO6iS7ju8c8rZeE9UtSivOyoLxS1CRtwLYDuMF5q7ZNA\n" +
            "FrWOnvPxC3JNAaSD1euMGocBb9gIOZG0Nw7VqXgKseE9g544cgFZB2S6Pxin\n" +
            "rMKugpFlVM3+NrmvEPtgXq6knrsSom0wT8EfExT0a0s/XZkurr6lncfDBgRX\n" +
            "qvaKAQgAw5WeuX1vHjNTR2RYjBN5zo8LqSianZ9dufHup5LwITI+3AxeN4OW\n" +
            "qWERsU1+x3Tgsn6IllvdU8in1G5Ha5lfkiVvolQXjph9Zxa7uo1gWKu5E5ro\n" +
            "+Cl6d58BRumNgP240t/BeIsCWdccE0ob5Xh71RuQNxwHwn63GWCnmXjTq/FX\n" +
            "x038qdfJF+94yjqxUMep6YK53nAHTDoejBarNsN4rRIFdeQv+bQGYCtd+CfE\n" +
            "7lBPA56oW8INwnWCa4sCvzWGtdBKJywmR0143U82Z6AfpkeptL2g9e0WioaV\n" +
            "+9GzKCcvjpzBu7VT9WTDm3uRdk3JJKzFWlwf15mp0Y/CT1hESwARAQAB/gkD\n" +
            "CP9DPEIVLMukYJ8V1XDXTazcez8gqrBadyEBzoNVxliGPl+NJmKqL0L3Tpor\n" +
            "AmJBhqeo6448jEi1aBWqPC9+WDEKK7ielox0RJF7QUIZHrI3WVFuKgU/zf1E\n" +
            "6+87/ri5KhUeLFlIQO7Ao4zlYIj7ME4nJqLKXMoakt546nAB3awdYs+7iYqh\n" +
            "RsbvlP+dMN3rYUbV4zFOXQ8CFT5evt2P8jplUQxmZMRRlRGeTdcycBDiTN1u\n" +
            "HK9xVxjp5gbiu7P7PsHRfQT8P9lPHrfCEY6JIpcgzBnKIQcsTH7S2mTiOP9g\n" +
            "uHuENpr838zybo27NbGi4NoppKzoVXddGP1RnShK4RZ2xZOxXsO74yeS/Ahk\n" +
            "tFeS8NRpyBOQmDN7yEzvbrODYZBlggwPcBqi/NQnqMtYmpb6Ah+vnQS0Rv4d\n" +
            "RvkevwTJ9E4Ea3p/MBmBiad091gkTMs3JVEXk0+2VjpQT/mjuCiG2XadcwkI\n" +
            "fYdKJAELqaJ12U2Xcon1GtHzqjvT3cOkTJ1NERjMIXD9Iz62YzC2JNlE0noB\n" +
            "yt70bhfpQk6KzDa7giP6032K1ExjvR81noyyagYp9SNzF8f2domKKvv/onR1\n" +
            "C7SU2f60tKVU3IJEafOMhOhPlM7/ZSD0236T4kaj/KXDshVE8izAZr6/CcdR\n" +
            "lB4tbpqvvIf1K5bYBahbxl7XSui29ITQcdnXIRS3VcdbGygOHpx6NF6FwkI1\n" +
            "zz9v6ML8ZA++vRmkuXiEYIY3zqZ0Nhg1juYDhYENej+XhS7L24jFhaG237xr\n" +
            "0DJKEZaqeaqkYjocJ6CqOK8CBpICw2vPGwm6en/gKHyEk4bmkVa9vUEd6gJH\n" +
            "OUkn5jLZmzt139+wg0FAs69WtU7d5vDF3MzTiCIoYMBbzYgGEjwCoVRocWr1\n" +
            "alLQ/7kJK/5wXSIqovED5ZMN+cLAXwQYAQgAEwUCV6r2jAkQlpXg8aSoVUkC\n" +
            "GwwAAIT5B/9x1JN6psoTClXxls6oU4x3BIYwjqJUkfzSLl1RQH/BX155vyPM\n" +
            "LZis5jJSX+SKRqKRDLzlHth9Pm0ffxDyGxwahHtUnA/ig/MyxCDumiSjQ11b\n" +
            "2yqozaCg9nGkDmWg0fj+pbKNigbRTEAgFW8Mjh/QV2bRv6pkveRlaKGt5kEY\n" +
            "iHX43Z/fNPfiO5tUsMA/AvzABBWaXPgSZmAGBlgiboApA8yF5EfwvvLs/GBv\n" +
            "j57VrJFRKjJ5rU81FUMe5NFtKsJMi68HLCU0+T5kTpQ2kgtMwzRlVchc5KM+\n" +
            "5KN07FBBnUEOOFdTw0WKKEvbJZ+hw4fkk5m8r3O18zcAkxdjOr8x\n" +
            "=Eu4O\n" +
            "-----END PGP PRIVATE KEY BLOCK-----";
}
