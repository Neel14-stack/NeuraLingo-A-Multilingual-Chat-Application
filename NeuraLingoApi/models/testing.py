import time
from concurrent.futures import ProcessPoolExecutor

def run(message):
    i = 5
    print("Running for " + message)
    time.sleep(1)
    i += 1
    print("Done for " + message + " " + str(i))


messages = ["name", "age"]


if __name__ == "__main__":
    # with ProcessPoolExecutor() as executor:
    #     executor.submit(run, messages[0])
    #     executor.submit(run, messages[1])
    a = run
    a("user")
    a("client")
    print("Whole Done")
