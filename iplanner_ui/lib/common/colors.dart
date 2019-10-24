import 'dart:math';

import 'package:flutter/material.dart';

const List EVENT_COLORS = [
  Colors.lightBlueAccent,
  Colors.lightGreenAccent,
  Colors.amberAccent,
  Colors.blueAccent,
  Colors.limeAccent
];
Random random = new Random();

Color getEventColor(String activity) {
  int index = random.nextInt(5);
  return EVENT_COLORS[index];
}
