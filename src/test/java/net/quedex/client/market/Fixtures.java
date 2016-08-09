package net.quedex.client.market;

public class Fixtures {
    private Fixtures() {}

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

    static final String ORDER_BOOK_STR = "{\"type\":\"data\",\"data\":\"-----BEGIN PGP SIGNED MESSAGE-----\\nHash: SHA256\\n\\n{\\n  \\\"instrument_id\\\" : 1,\\n  \\\"bids\\\" : [ [ 0.00142858, 1 ] ],\\n  \\\"asks\\\" : [ [ 0.00166666, 1 ], [ 0.00166944, 3 ] ],\\n  \\\"type\\\" : \\\"order_book\\\",\\n  \\\"id\\\" : 7926\\n}\\n-----BEGIN PGP SIGNATURE-----\\nVersion: QPG\\n\\niQFEBAEBCAAuBQJXqM9dJxxRdWVkZXggVGVzdCBLZXkgMSA8Y29udGFjdEBxdWVk\\nZXgubmV0PgAKCRDuvRcebSdFGCKGCACKDz/vl7rXyESu6qXkffKY3H01AZO2Gv5h\\n58U9X/erKQrDV6f31fmJiqV93LbU2pTf7ueoytmxUB2PTtn622QiBv1dGzQOy0tp\\nuDve6B1VctoLaBKUEvV2PhtQ3fimXu78RHtXTahTwyR6F6QEa6xYcBz9eyBJlkBT\\nDmMo+JpZRBYPtkaBEurrjRknA1ZE+AdVC5/BIaGIN8+cvA7dSnaKEEdp9+NXgmtP\\nWyC1CiP/RYLqcepHOZQlbt+Es5MFtgfnAxQs5EE+/R5Zp1tMRI3ym93qStd96/PL\\nEhl1ZLEJ1b+s0W0jPTfWRCx0sFLbINOdTn1PGLboYPtFgyr6/D8I\\n=yW+f\\n-----END PGP SIGNATURE-----\\n\",\"id\":70}";

    static final String QUOTES_STR = "{\"type\":\"data\",\"data\":\"-----BEGIN PGP SIGNED MESSAGE-----\\nHash: SHA256\\n\\n{\\n  \\\"instrument_id\\\" : 1,\\n  \\\"last\\\" : 0.00142858,\\n  \\\"last_quantity\\\" : 1,\\n  \\\"bid\\\" : null,\\n  \\\"bid_quantity\\\" : null,\\n  \\\"ask\\\" : 0.00166944,\\n  \\\"ask_quantity\\\" : 3,\\n  \\\"volume\\\" : 2,\\n  \\\"open_interest\\\" : 0,\\n  \\\"type\\\" : \\\"quotes\\\"\\n}\\n-----BEGIN PGP SIGNATURE-----\\nVersion: QPG\\n\\niQFEBAEBCAAuBQJXqgXmJxxRdWVkZXggVGVzdCBLZXkgMSA8Y29udGFjdEBxdWVk\\nZXgubmV0PgAKCRDuvRcebSdFGIelCACWHwNLVeb0Jvrhj/DFNEFCW9JaGndlyGAa\\nW3oRO7YHKteoJRuEDXa8azRwqaXJRQoBzmNJPd8RLPyk3sq2KfN2OJjvnzVd3zCe\\nzts3BOr1aoRjqngDdvHXmE3kfDWe/mh3QFFDrnRChVzYZFa3i0OGWKydQGavBXet\\nhyZwZGiWXPOAczBSg6A3N0SsMUvM3DwYDbzqjf1QeDmbdcHXo8gIUb6on3BWWH8S\\ntWKKkpGfKYAMwyiqQEFvEGRHWbcQhsWSc2N9a12fSKNQaNZyrcvL2/SEwrWlUEzK\\nfov0mKjNu9prYucZeiz8r8lZ0aRlV16lQHb/JObc5JWha+Yt+wQ6\\n=dPC/\\n-----END PGP SIGNATURE-----\\n\",\"id\":1}/oCUqgwQ5KodnBLfj1i/9lV1noWLbnFgZ7u\\ns3ESv1JvwbPJyZ+QipaiBf51t1mK78aHw8tHrLJUiFdaReCLYhlbkC/Iwn5ZFq6P\\nZoHPsr+89v63uItLL+CRqG6FM2yH5vNS5XNpJl3seZCxNDzh+E1njYDBBepKO9Il\\nU08h+mlQpotAIbX5KYeDDF60mAN64GWNqbV7AFhr4dmabBPJqI2beD4IlQkd8+DI\\nwOJHNaxnwRjPtQw9Z0y1KlfJTNeH+Zf9WG/A30EZzgl0j0WDr/TsR8sRj7ARIKH+\\n3WlLDWVM6Q4csF0MGrsYSVr3vPQObwkoHbHnm894xParBG8lulJu\\n=kWOL\\n-----END PGP SIGNATURE-----\\n\",\"id\":1}";

    static final String TRADE_STR = "{\"type\":\"data\",\"data\":\"-----BEGIN PGP SIGNED MESSAGE-----\\nHash: SHA256\\n\\n{\\n  \\\"instrument_id\\\" : 1,\\n  \\\"trade_id\\\" : 70,\\n  \\\"timestamp\\\" : 1470681720788,\\n  \\\"price\\\" : 0.00166666,\\n  \\\"quantity\\\" : 1,\\n  \\\"liquidity_provider\\\" : \\\"seller\\\",\\n  \\\"type\\\" : \\\"trade\\\",\\n  \\\"id\\\" : 8568\\n}\\n-----BEGIN PGP SIGNATURE-----\\nVersion: QPG\\n\\niQFEBAEBCAAuBQJXqNJ4JxxRdWVkZXggVGVzdCBLZXkgMSA8Y29udGFjdEBxdWVk\\nZXgubmV0PgAKCRDuvRcebSdFGIQaCACY6H9rO3/Y/ktHgR8dSUtmvCBpglnGW7u1\\nycOrMzWKJuNBE/x0foX4WTI/G+GS2/TXt3wbwn2sNpVNi8ehjPOqRWL+NOQO7w/2\\nZpAWijeLbZtn7mrEI3Gf7MQzWxM/8o94MgmxEIfMSb6t7U5/+ii6MuE95t5dDh7g\\nRLCvdyHaGVLit0fRJySYu9yneN0lueU/uEXztRwsmgfQtl7R3De0odgGimIa33kt\\nBWzmrG9Kww9ActdbB5lEdmPuFeHIPL/QHEOaJmB1YDBlL4ezenK9OZ4xHjN1mDDQ\\nq/C+Kijm03MrwCa1pfeXd/YPpwzCOGbWY8nK+S1751+SNPS1npMQ\\n=M0Qf\\n-----END PGP SIGNATURE-----\\n\",\"id\":139}";

    static final String SESSION_STATE_STR = "{\"type\":\"data\",\"data\":\"-----BEGIN PGP SIGNED MESSAGE-----\\nHash: SHA256\\n\\n{\\n  \\\"state\\\" : \\\"auction\\\",\\n  \\\"type\\\" : \\\"session_state\\\",\\n  \\\"id\\\" : 8572\\n}\\n-----BEGIN PGP SIGNATURE-----\\nVersion: QPG\\n\\niQFEBAEBCAAuBQJXqNKAJxxRdWVkZXggVGVzdCBLZXkgMSA8Y29udGFjdEBxdWVk\\nZXgubmV0PgAKCRDuvRcebSdFGFQ7CACkEwgb+BiZTUplp7v59pJxS9kEpcLll2Cv\\nrRuY1eRiOEYNogxP+4emedOBDEdVX4F++ePebmsfPvvMbhZ8ZGx/lM1dAyG+H2kX\\nABBPhW8Okm5gj/RArkqzUMAVM8DXQSpNm7YOUuNijGrtLg7EJw3NMQd5mPG9eXb4\\njJuPooT/vtWemDqHWEGFE89aQOXSsi+Qi+gMJgyPSjAwRYnC/g6aFHZYuF/iO7W8\\nBisiX+2UmVzARhqscbJ9FszfHFGv8ornZYmkEaPlXcNpF0zc5ZBDc6Gqmy8QBS7h\\nQBvtZvcmdXyIOiBEnd9gwatydnhJr+YzFSGkchFzrcuB+ORXfldV\\n=1zmH\\n-----END PGP SIGNATURE-----\\n\",\"id\":140}";

    static final String SESSION_STATE_STR_WRONG_SIG = "{\"type\":\"data\",\"data\":\"-----BEGIN PGP SIGNED MESSAGE-----\\nHash: SHA256\\n\\n{\\n  \\\"state\\\" : \\\"auction\\\",\\n  \\\"type\\\" : \\\"session_state\\\",\\n}\\n-----BEGIN PGP SIGNATURE-----\\nVersion: QPG\\n\\niQFEBAEBCAAuBQJXqNKAJxxRdWVkZXggVGVzdCBLZXkgMSA8Y29udGFjdEBxdWVk\\nZXgubmV0PgAKCRDuvRcebSdFGFQ7CACkEwgb+BiZTUplp7v59pJxS9kEpcLll2Cv\\nrRuY1eRiOEYNogxP+4emedOBDEdVX4F++ePebmsfPvvMbhZ8ZGx/lM1dAyG+H2kX\\nABBPhW8Okm5gj/RArkqzUMAVM8DXQSpNm7YOUuNijGrtLg7EJw3NMQd5mPG9eXb4\\njJuPooT/vtWemDqHWEGFE89aQOXSsi+Qi+gMJgyPSjAwRYnC/g6aFHZYuF/iO7W8\\nBisiX+2UmVzARhqscbJ9FszfHFGv8ornZYmkEaPlXcNpF0zc5ZBDc6Gqmy8QBS7h\\nQBvtZvcmdXyIOiBEnd9gwatydnhJr+YzFSGkchFzrcuB+ORXfldV\\n=1zmH\\n-----END PGP SIGNATURE-----\\n\",\"id\":140}";

    static final String ERROR_MAINTENANCE_STR = "{\"type\":\"error\",\"error_code\":\"maintenance\"}";
}
