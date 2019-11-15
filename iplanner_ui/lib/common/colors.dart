import 'dart:math';

import 'package:flutter/material.dart';

const List EVENT_COLORS = [
  Colors.lightBlueAccent,
  Colors.lightGreenAccent,
  Colors.amberAccent,
  Colors.blueAccent,
  Colors.limeAccent
];

Color getTodoColor(String activity) {
  Random random = new Random(17);
  int index = random.nextInt(5);
  return EVENT_COLORS[index];
}
