import sys
import subprocess

def install(package):
    subprocess.check_call([sys.executable, "-m", "pip", "install", package])

try:
    import pandas as pd
except ImportError:
    install('pandas')
    import pandas as pd

try:
    import openpyxl
except ImportError:
    install('openpyxl')

data = [
    {"Mã Tổ Hợp": "A00", "Môn 1": "Toán", "Môn 2": "Vật lí", "Môn 3": "Hóa học", "Tên Tổ Hợp": "Toán, Vật lí, Hóa học"},
    {"Mã Tổ Hợp": "A01", "Môn 1": "Toán", "Môn 2": "Vật lí", "Môn 3": "Tiếng Anh", "Tên Tổ Hợp": "Toán, Vật lí, Tiếng Anh"},
    {"Mã Tổ Hợp": "A02", "Môn 1": "Toán", "Môn 2": "Vật lí", "Môn 3": "Sinh học", "Tên Tổ Hợp": "Toán, Vật lí, Sinh học"},
    {"Mã Tổ Hợp": "B00", "Môn 1": "Toán", "Môn 2": "Hóa học", "Môn 3": "Sinh học", "Tên Tổ Hợp": "Toán, Hóa học, Sinh học"},
    {"Mã Tổ Hợp": "C00", "Môn 1": "Ngữ văn", "Môn 2": "Lịch sử", "Môn 3": "Địa lí", "Tên Tổ Hợp": "Ngữ văn, Lịch sử, Địa lí"},
    {"Mã Tổ Hợp": "C01", "Môn 1": "Ngữ văn", "Môn 2": "Toán", "Môn 3": "Vật lí", "Tên Tổ Hợp": "Ngữ văn, Toán, Vật lí"},
    {"Mã Tổ Hợp": "C02", "Môn 1": "Ngữ văn", "Môn 2": "Toán", "Môn 3": "Hóa học", "Tên Tổ Hợp": "Ngữ văn, Toán, Hóa học"},
    {"Mã Tổ Hợp": "D01", "Môn 1": "Ngữ văn", "Môn 2": "Toán", "Môn 3": "Tiếng Anh", "Tên Tổ Hợp": "Ngữ văn, Toán, Tiếng Anh"},
    {"Mã Tổ Hợp": "D07", "Môn 1": "Toán", "Môn 2": "Hóa học", "Môn 3": "Tiếng Anh", "Tên Tổ Hợp": "Toán, Hóa học, Tiếng Anh"},
    {"Mã Tổ Hợp": "D08", "Môn 1": "Toán", "Môn 2": "Sinh học", "Môn 3": "Tiếng Anh", "Tên Tổ Hợp": "Toán, Sinh học, Tiếng Anh"}
]

df = pd.DataFrame(data)
df.to_excel("ToHopMon_Mau.xlsx", index=False)
print("Successfully generated ToHopMon_Mau.xlsx!")
