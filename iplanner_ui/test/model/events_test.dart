import 'dart:convert';

import 'package:flutter_test/flutter_test.dart';
import 'package:iplanner_ui/model/event_list.dart';
import 'package:resource/resource.dart' show Resource;

Future<String> loadEventJson() async {
  var uri = new Uri.file('test/resources/events.json');
  var resource = new Resource(uri.toString());

  return resource.readAsString(encoding: utf8);
}

void main() {
  group("Events Model Test", () {
    test("test Event.fromJson", () async {
      var jsonStr = await loadEventJson();

      var list = (json.decode(jsonStr) as List)
          .map((e) => new Event.fromJson(e))
          .toList();

      print(list);
    });
  });
}
