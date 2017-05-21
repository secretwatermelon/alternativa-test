from faker import Faker
import random
fake = Faker()

lastEntryTime = 0
for i in range(1000):
    entryTime = lastEntryTime + random.randint(1, 2500)
    lastEntryTime = entryTime
    print("{},{},{}".format(fake.name().replace(' ', ''), random.randint(1,30), entryTime))
