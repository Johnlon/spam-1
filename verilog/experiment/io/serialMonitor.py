#!/usr/bin/python3
import time
import string
from pathlib import Path
import sys
import threading
import time
import platform
import os
import stat
from os import _exit
from os import mkfifo
from os import path

home = str(Path.home())

# compat
try:
    import readline
except ImportError:
    import pyreadline as readline


def loadLastFifoName():
    try:
        with open(home + "/.uartmonitor") as f:
            content = f.readlines()

        if len(content) > 0:
            return content[0].strip()
        else:
            return "/tmp/fifo"
    except:
        pass


def saveFifoName(comNum):
    with open(home + "/.uartmonitor", "w") as f:
        f.write(comNum)


def selectFifo():
    default = loadLastFifoName()

    print("fifo name [%s] ? " % default, end="")
    sys.stdout.flush()

    c = sys.stdin.readline().strip()
    if c.strip() != "":
        com = c
    elif default:
        com = default
    else:
        print("must select fifo name ")
        return selectFifo()

    saveFifoName(com)

    if com.isdigit():
        if (platform.system() == "Linux"):
            fifoName = '/tmp/fifo'
        else:
            print("fifo's only work on *Nix")
            _exit(1)
    else:
        fifoName = com

    return fifoName


class DuplexFifoInterface():

    def _recvResponse(resp):
        print("%s" % resp, end='')
        sys.stdout.flush()

    def __init__(self, fifoName, *args, **kwargs):
        self.responseHandler = None
        self.fifoTx = None
        self.fifoRx = None

        self.fifoName = fifoName
        self.fifoTxName = fifoName + ".tx"
        self.fifoRxName = fifoName + ".rx"

        self.serialThreadRunning = False

    def _openFifo(self, fifo, fifoName, openMode):
        # self.responseHandler("connecting to " + fifoName + " for " + str(openMode) + "\n")
        if fifo:
            try:
                self.responseHandler("closing " + fifoName + " fd " + str(fifo) + "\n")
                fifo.close()
            except BaseException as e:
                self.responseHandler("closing " + fifoName + " error ", e)

            time.sleep(1)

        if path.exists(fifoName):
            # self.responseHandler("checking " + fifoName + "\n")
            if not stat.S_ISFIFO(os.stat(fifoName).st_mode):
                self.responseHandler("found " + fifoName + " but it is not a fifo\n")
                _exit(1)
            # self.responseHandler("found fifo " + fifoName + "\n")
            
        else:
            os.mkfifo(fifoName, mode = 0o600)

        # self.responseHandler("opening " + fifoName + "\n")
        fifo = open(fifoName, openMode)# | os.O_NONBLOCK) #, mode=openMode)
        # self.responseHandler("opened " + fifoName + "  fd=" + str(fifo) + "\n")

        return fifo

    # receive from fifo
    def openRxFifo(self, responseHandler=_recvResponse):
        self.responseHandler = responseHandler
        self.fifoRx = self._openFifo(self.fifoRx, self.fifoRxName, "r") #self.fifoRxName, os.O_RDONLY | os.O_NONBLOCK)# "r")
        
    # write to fifo
    def openTxFifo(self, responseHandler=_recvResponse):
        self.fifoTx = self._openFifo(self.fifoTx, self.fifoTxName, "w") #self.fifoTxName, os.O_WRONLY)#  |os.O_NONBLOCK)# "w")
        
    def close(self):
        if self.fifoRx:
            self.fifoRx.close()
        if self.fifoTx:
            self.fifoTx.close()

    def _keyboardInput(self):

        try:
            while True:
                print("input:")
                #line = sys.stdin.readline().strip()
                line = input("").strip()
                if (line == "q"):
                    break

                # write to device
                if len(line) > 0:
                    print("writing to out " + line)
                    self._writeToFifo(line)

            print("QUIT")
            _exit(0)
        except BaseException as x:
            print("ERR READING STDIN\n")
            print(x)
            raise x
            _exit(1)

    def _readSerial(self):
        self.serialThreadRunning = True
        current = self.fifoRx
        while self.fifoRx and self.fifoRx == current:
            msg = None
            try:
                msg = self.fifoRx.readline()
                #msg = msg.decode("utf-8")  # .strip()
            except BaseException as e:
                self.responseHandler("ERR: while reading from  : %s %s\n" % (
                    self.fifoRxName, (type(e), str(e))))
                if msg:
                    self.responseHandler("ERR: read '%s' + \n" % msg)
                break

            if msg and (len(msg) > 0):
                self.responseHandler(msg)

        self.serialThreadRunning = False

    def startTxThread(self):

        if not self.fifoTx:
            raise Exception("Tester " + self.fifoTxName + " not open")

        # thread to read local keyboard input
        input_thread = threading.Thread(target=self._keyboardInput)
        input_thread.daemon = False
        input_thread.start()

    def startRxThread(self):

        if not self.fifoRx:
            raise Exception("Tester " + self.fifoRxName + " not open")

        # thread to read and print data from arduino
        sinput_thread = threading.Thread(target=self._readSerial)
        sinput_thread.daemon = True
        sinput_thread.start()

    def _writeToFifo(self, testcase):
        try:
            # self.openTxFifo()
            self.fifoTx.write(testcase)
            self.fifoTx.write("\n")
            self.fifoTx.flush()
            # self.fifoTx.close()
        except BaseException as x:
            self.responseHandler("EXCEPTION : " + str(x))


# Support for CTRL-C needs main thread still running.
# This is actually a crucial step, otherwise all signals sent to your program will be ignored.
# Adding an infinite loop using time.sleep() after the threads have been started will do the trick:
def main():
    com = selectFifo()

    tester = DuplexFifoInterface(com)

    try:
        tester.openRxFifo()
        tester.openTxFifo()
        tester.startRxThread()
        tester.startTxThread()

        while True:
            if not tester.serialThreadRunning:
                print("kb thread not running ..")
                try:
                    tester.close()
                    tester.openRxFifo()
                    tester.openTxFifo()
                    tester.startRxThread()
                    tester.startTxThread()
                except BaseException as e:
                    print("error while reopening", e)
                    raise e
                    pass

        time.sleep(1)
    except KeyboardInterrupt:
        _exit(1)
    # except BaseException as x:
    #     try:
    #         print("ERR4 ", x)
    #         tester.close()
    #         sys.stdin.close()
    #         _exit(1)
    #     except:
    #         _exit(1)


if __name__ == '__main__':
    os.system('stty sane')
    main()
