import 'dart:convert';

import 'package:flutter_test/flutter_test.dart';
import 'package:iplanner_ui/model/activity_list.dart';
import 'package:resource/resource.dart' show Resource;

Future<String> loadActivityJson() async {
  var uri = new Uri.file('test/resources/activities.json');
  var resource = new Resource(uri.toString());

  return resource.readAsString(encoding: utf8);
}

void main() {
  group("Activities Model Test", () {
    test("test Activity.fromJson", () async {
      var jsonStr = await loadActivityJson();

      var list = (json.decode(jsonStr) as List)
          .map((e) => new Activity.fromJson(e))
          .toList();

      print(list);
    });
  });
}
