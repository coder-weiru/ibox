import 'dart:convert';

import 'package:flutter_test/flutter_test.dart';
import 'package:iplanner_ui/model/todo_list.dart';
import 'package:resource/resource.dart' show Resource;

Future<String> loadTodoJson() async {
  var uri = new Uri.file('test/resources/todos.json');
  var resource = new Resource(uri.toString());

  return resource.readAsString(encoding: utf8);
}

void main() {
  group("Todos Model Test", () {
    test("test Todo.fromJson", () async {
      var jsonStr = await loadTodoJson();

      var list = (json.decode(jsonStr) as List)
          .map((e) => new Todo.fromJson(e))
          .toList();

      print(list);
    });
  });
}
