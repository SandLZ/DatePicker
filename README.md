# 日期选择控件

## Platform

- ios
- android

## Usage

```js
var options = {
    date: new Date(),
    mode: 'date',
    type: 2
};

function onSuccess(date) {
    alert('Selected date: ' + date);
}

function onError(error) { // Android only
    alert('Error: ' + error);
}
datePicker.show(options, onSuccess, onError);
```

## Options

类型

- 0 时间
- 1 日期+时间
- 2 年-月-日
- 3 年-月
- 4 年
- 5 日期+时间 (强化)
- 6 周

调用示例

说明：
1. type             int         类型
2. date             string      日期(2016-10-09)
3. showSecond       bool        显示秒？
4. weekIndex        int         显示的周下标(0 开始)
5. weeKMaxIndex     int         显示的周最大下标(-1 默认)
6. weekNameArr      array       显示的数据
7. maxDate          string      最大时间

```js
$scope.goDatePicker = function (index) {
      var date = new Date();
      var options = {
          showSecond:true,
          weekIndex:0,
          weeKMaxIndex: 10,
          weekNameArr:tempData,
          type:index,
          date: date,
          mode: 'date',
          locale: 'zh_CN',
          //ios
          cancelButtonLabel: '取消',
          doneButtonLabel: '确定',
          maxDate: new Date().getTime(),
          //android
          okText: '确定',
          cancelText: '取消'
        };
      };

      function onSuccess(date) {
        if (date != undefined && date.length > 0) {
          console.log('选择的时间 -  '+date);
        }else {
          console.log('取消选择');
        }
      }

      function onError(error) { // android
        console.log(('错误' + error));
      }
      datePicker.show(options, onSuccess, onError);
    };

```


date (string)

- new Date()
- "2016-10-09"

onSuccess(date)

date - string
根据传入的type取相应格式的日期
