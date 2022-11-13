from sqlalchemy import create_engine


class SQLTransactions():

    db = create_engine(
        'postgresql://postgres:polkabulok56@qiwi_db:5432/qiwi_api_db')
    conn = db.connect()

    def __init__(self, accountid=None, requestid=None, phone=None,
                 refresh_token=None, access_token=None):

        self.accountid = accountid
        self.requestid = requestid
        self.phone = phone
        self.refresh_token = refresh_token
        self.access_token = access_token

    def validatePhone(self):
        print('2.5')
        db_answer = self.db.execute("SELECT accountid FROM api_userinfo "
                                    "WHERE phone='{}';".
                                    format(self.phone)).first()
        print('2.9')
        return False if db_answer is None else True

    def getAccessTokenByPhone(self):
        kick = self.db.execute("SELECT access_token FROM api_userinfo "
                               "WHERE phone='{}';".format(self.phone)).first(
                                                                    )['phone']
        return kick

    def createUserbyPhone(self):
        self.db.execute("INSERT INTO api_userinfo "
                        "(phone, requestid) "
                        "VALUES ('{}', '{}');".format(self.phone,
                                                      self.requestid))


    def findAccountID(self):
        return self.db.execute("SELECT accountid FROM api_userinfo "
                        "WHERE phone='{}';".format(self.phone)).first(
                                                        )['accountid']

    def addTokens(self):
        self.db.execute("UPDATE api_userinfo "
                        "SET refresh_token='{}', "
                        "access_token='{}' "
                        "WHERE requestid='{}';".format(self.refresh_token,
                                                       self.access_token,
                                                       self.requestid))
        print("UPDATE api_userinfo "
                        "SET refresh_token='{}', "
                        "access_token='{}' "
                        "WHERE requestid='{}';".format(self.refresh_token,
                                                       self.access_token,
                                                       self.requestid))

    def findTokenAndAccount(self):
        return self.db.execute("SELECT refresh_token, accountid "
                               "FROM api_userinfo "
                               "WHERE access_token='{}';".format(
                                self.access_token)).first()

    def findRefreshToken(self):
        self.db.execute("SELECT refresh_token FROM api_userinfo"
                        "WHERE accountid={};".format(self.accountid)).first(
                            ['accountid']
                        )

    def findRefreshByAccess(self):
        self.db.execute("SELECT refresh_token FROM api_userinfo"
                        "WHERE access_token='{}';".format(
                            self.access_token)).first(
                            ['refresh_token']
                        )