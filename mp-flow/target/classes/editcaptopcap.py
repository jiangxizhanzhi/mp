import os
import sys

filename = sys.argv[1]
os.system("editcap " + filename + " " + filename + ".c")
os.system("mv " + filename + ".c" + " " + filename)
