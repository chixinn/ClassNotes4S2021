from student_util import *
from web3_deploy import *

point = 0 # 测试分值

def add_point(val, msg):
    global point
    point += val
    print_success("{}\n\t\t==> Get {} point, current point: {}".format(msg, val, point))

# 合约部署者即为管理员，后面有对管理员的测试，只有管理员才可以进行插入操作
try:
    student_contract = compile_and_deploy("StudentContract") # 编译并部署合约
    add_point(20, "compile_and_deploy StudentContract success!")
except:
    print_error("compile_and_deploy StudentContract error!")

num = 10
students = random_student_list(num) # 获取随机生成学生列表
actual_num = 0


# 测试插入
for student in students:
    try:
        # 构造函数通过rpc向以太坊发送该函数交易
        # *为解包操作，即传入的参数为: id, name, sex, age, dept
        tx_hash = student_contract.functions.insert(*student).transact()
        tx_receipt = eth.waitForTransactionReceipt(tx_hash)
        add_point(5, "insert {} success!".format(student))
        actual_num += 1
        # 测试获取日志
        try:
            insert_event = student_contract.events.Insert.getLogs()[0]
            id = insert_event.get('args').get('id')
            if student[0] == id:
                add_point(5, "emit event success! event args: {}".format(insert_event.get('args')))
            else:
                raise Exception
        except Exception as e:
            print_error("error: {}\nget event error! id = {}".format(e, student[0]))
    except Exception as e:
        print_error("error: {}\ninsert error! student: {}".format(e, student))

if actual_num == num:
    add_point(10, "actual_num(={}) == num(={}) success!".format(actual_num, num))
else:
    print_error("actual_num(={}) == num(={}) error!".format(actual_num, num))


# 测试查询
# 测试 select count，返回合约中学生的总数
try:
    select_count = student_contract.functions.select_count().call()
    if actual_num == select_count:
        add_point(10, "actual_num(={}) == select_count(={}) success!".format(actual_num, select_count))
    else:
        print_error("actual_num(={}) == select_count(={}) error!".format(actual_num, select_count))
except Exception as e:
    print_error("error: {}\nselect_count error!".format(e))

# 测试 select all id，返回合约中所有id构成的数组
all_id = []
try:
    all_id = student_contract.functions.select_all_id().call()
    add_point(20, "select_all_id(={}) success!".format(all_id))
except Exception as e:
    print_error("error: {}\nselect_all_id error!".format(e))

# 测试 select id，返回指定id的学生的信息元组
for id in all_id:
    try:
        student = student_contract.functions.select_id(id).call()
        student = tuple(student)
        add_point(2, "select_id(id={}) success!".format(id))
        if student == students[id]:
            add_point(5, "{} == {} success!".format(student, students[id]))
        else:
            print_error("{} == {} error!".format(student, students[id]))
    except Exception as e:
         print_error("error: {}\nselect_id(id={}) error!".format(e, id))

# 测试是否只有管理员才可进行插入操作
eth.default_account = eth.accounts[1]
try:
    func_tx = student_contract.functions.insert(*student)
    try:
        tx_hash = func_tx.transact()
        tx_receipt = eth.waitForTransactionReceipt(tx_hash)
        print_error("only admin error!")
    except:
        add_point(10, "only admin success!")
except:
    print_error("insert func error!")

# 打印最终点数
print("point: {}".format(point))