字符串内容说明

    SE的文本以UTF-8编码的字符串为主
    中间可能会插入以02开头03结尾的预留串 
    例子如下
    0x02 <类型> <长度> 0x00 ... 0x00 0x03

1.自定义Transtring翻译规则 优先级2

	程序可以通过conf/transtring.properties的K/V设置
	进行翻译的修正以及填充
	只用将需要翻译的内容通过键值串方式写入文件保存即可
	仅对普通字符串类型的内容有效
	对内置预留串的内容无效
	
2.自定义Transtable翻译规则 优先级1

	程序可以通过conf/transtable.properties的K/V设置
	进行外部字符的修正以及填充
    Key的格式为 文件名(不带扩展名)_行数_字符串(类型为 0x00)列数
    例如exd/mounttransient_99_2
    就代表exd/mounttransient文件
    第99行 第2个字符串列
    Value为byte[]的Base64编码字符串
    适用于任何字符串table的内容填充
	
例子：

    使用4.0的CN客户端资源包对4.1的JP资源包汉化后
    会发现莽族友好度部分名称显示内容异常
    
    观察exd/beasttibe文件
    对比CN和JP文件发现字符串所在列有差异
    CN->全表第11列 字符串第1列
    JP->全表第10列 字符串第1列
    然而在JP端内蛮族友好度名称显示为全表10列的内容
    
    对比JP/EN表发现JP表结构无问题 文件内容无异常
    推断为调用表坐标因为填充问题覆盖为CN标准
    文件内搜索beasttibe关键字
    在addon文件内102517行搜索到目标
    对比CN和JP的内容
    CN->022810FF0B42656173745472696265E8020B03(HexString)
    JP->022810FF0B42656173745472696265E8020C03(HexString)
    得到差异原因
    
    解决方案：
    将022810FF0B42656173745472696265E8020C03(HexString)转换成Base64
    然后黏贴进trastable.properties文件内
    exd/addon_102517_1=AigQ/wtCZWFzdFRyaWJl6AIMAw==
    还原并重新汉化文件 问题解决