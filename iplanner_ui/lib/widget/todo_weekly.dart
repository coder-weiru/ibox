import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:iplanner_ui/common/colors.dart';
import 'package:iplanner_ui/model/todo_list.dart';

class TodoWeekly extends StatelessWidget {
  final Map<String, List<Todo>> _todoMap;

  const TodoWeekly({@required todoMap}) : _todoMap = todoMap;

  Map<String, List<Todo>> get todos => this._todoMap;

  final List<String> _weekDays = const [
    'Sun',
    'Mon',
    'Tue',
    'Wed',
    'Thu',
    'Fri',
    'Sat'
  ];

  Widget _buildRow(BuildContext context, int index) {
    final activity = _todoMap.keys.elementAt(index);
    final todoList = this._todoMap[activity];
    return Container(
        height: 60.0,
        decoration: BoxDecoration(
          color: Color.fromRGBO(223, 230, 233, 0.5),
        ),
        child: GridView.builder(
            gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: _weekDays.length,
            ),
            itemCount: _weekDays.length,
            physics: NeverScrollableScrollPhysics(),
            itemBuilder: (context, index) {
              final todo = getTodoByWeekDay(todoList, index);
              return Container(
                  decoration: BoxDecoration(
                    color: Color.fromRGBO(223, 230, 233, 0.5),
                    border:
                        Border.all(color: Colors.lightBlueAccent, width: 0.5),
                  ),
                  child: Visibility(
                      visible: todo != null,
                      child: Center(
                          child: new Card(
                              color: getTodoColor(activity),
                              elevation: 5.0,
                              child: new Container(
                                  alignment: Alignment.center,
                                  margin: new EdgeInsets.only(
                                      top: 2.0,
                                      bottom: 2.0,
                                      left: 2.0,
                                      right: 2.0),
                                  child: Tooltip(
                                      message: todo != null
                                          ? new DateFormat("EEEE, MMMM d")
                                              .format(todo.start)
                                          : "",
                                      child: new Text(
                                          todo != null ? todo.summary : "",
                                          style: TextStyle(
                                            color: Colors.black45,
                                            fontSize: 10.0,
                                            fontWeight: FontWeight.bold,
                                          ))))))));
            }));
  }

  @override
  Widget build(BuildContext context) {
    return Container(
        padding: EdgeInsets.all(5.0),
        child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            mainAxisSize: MainAxisSize.min,
            children: <Widget>[
              Container(
                  padding: EdgeInsets.all(5.0),
                  height: 60.0,
                  decoration: BoxDecoration(
                    color: Color.fromRGBO(223, 230, 233, 0.5),
                    borderRadius: BorderRadius.circular(25.0),
                  ),
                  child: GridView.builder(
                      gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                        crossAxisCount: _weekDays.length,
                      ),
                      itemCount: _weekDays.length,
                      itemBuilder: (context, index) {
                        return Center(
                            child: new Card(
                                color: Colors.amberAccent,
                                elevation: 5.0,
                                child: new Container(
                                    alignment: Alignment.center,
                                    margin: new EdgeInsets.only(
                                        top: 5.0,
                                        bottom: 5.0,
                                        left: 5.0,
                                        right: 5.0),
                                    decoration: new BoxDecoration(
                                      color: Colors.white54,
                                      shape: BoxShape.circle,
                                    ),
                                    child: new Text(
                                      _weekDays[index],
                                      style: TextStyle(
                                        color: Colors.redAccent,
                                        fontSize: 18.0,
                                        fontWeight: FontWeight.bold,
                                      ),
                                    ))));
                      })),
              Expanded(
                child: // Todos by activities
                    Container(
                        decoration: BoxDecoration(
                          color: Color.fromRGBO(223, 230, 233, 0.5),
                          borderRadius: BorderRadius.circular(25.0),
                        ),
                        child: new ListView.builder(
                            itemCount: _todoMap.keys.toList().length,
                            itemBuilder: (BuildContext context, int index) {
                              return _buildRow(context, index);
                            })),
              )
            ]));
  }
}

Todo getTodoByWeekDay(List<Todo> todos, int weekdayIndex) {
  return todos.firstWhere((todo) {
    final start = todo.start;
    return start.weekday == weekdayIndex;
  }, orElse: () => null);
}
