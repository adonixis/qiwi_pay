from sqlalchemy import create_engine
db = create_engine(
    'postgresql://postgres:polkabulok56@localhost:5432/qiwi_api_db')
conn = db.connect()


print(db.execute("SELECT refresh_token, accountId "
                        "FROM api_userinfo "
                        "WHERE access_token='{}';".format('mmmmmm')).first())
                        