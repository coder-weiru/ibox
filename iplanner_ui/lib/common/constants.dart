import 'package:flutter/material.dart';

// *Note*: when APP_VERSION is changed, remember to also update pubspec.yaml.
const APP_VERSION = 'v0.0.1';
const APP_NAME = 'i-Planner';
const APP_DESCRIPTION = 'An app helps manage schedule and tasks.'
    '\n\nDeveloped by Chris Ru.';

const HEADER_HEIGHT = 128.0;
const MENU_EVENTS = 'My Events';
const MENU_ACTIVITIES = 'My Activities';

const TEXT_TITLE_FONT = const TextStyle(fontSize: 20.0);
const TEXT_SUBTITLE_FONT = const TextStyle(fontSize: 16.0);

final Widget splash = Image.asset(
  'assets/images/home-office.jpg',
  fit: BoxFit.cover,
);

final Widget imageAtom = Image.asset(
  'assets/images/atom.jpg',
  fit: BoxFit.fill,
);

final Widget imageBackToSchool = Image.asset(
  'assets/images/back-to-school.jpg',
  fit: BoxFit.fill,
);

final Widget imageBrain = Image.asset(
  'assets/images/brain.jpg',
  fit: BoxFit.fill,
);

final Widget imageBrainstorm = Image.asset(
  'assets/images/brainstorm.jpg',
  fit: BoxFit.fill,
);

final Widget imageBulbs = Image.asset(
  'assets/images/bulbs.jpg',
  fit: BoxFit.fill,
);

final Widget imageExhausted = Image.asset(
  'assets/images/exhausted.jpg',
  fit: BoxFit.fill,
);

final Widget imageLearn = Image.asset(
  'assets/images/learn.jpg',
  fit: BoxFit.fill,
);

final Widget imageMan_1 = Image.asset(
  'assets/images/man_1.jpg',
  fit: BoxFit.fill,
);

final Widget imageMan_2 = Image.asset(
  'assets/images/man_2.jpg',
  fit: BoxFit.fill,
);

final Widget imageOffice = Image.asset(
  'assets/images/office.jpg',
  fit: BoxFit.fill,
);
final Widget imagePaper = Image.asset(
  'assets/images/paper.jpg',
  fit: BoxFit.fill,
);
final Widget imageQuestions = Image.asset(
  'assets/images/questions.jpg',
  fit: BoxFit.fill,
);
final Widget imageReading = Image.asset(
  'assets/images/reading.jpg',
  fit: BoxFit.fill,
);
final Widget imageSchool_1 = Image.asset(
  'assets/images/school_1.jpg',
  fit: BoxFit.fill,
);
final Widget imageSchool_2 = Image.asset(
  'assets/images/school_2.jpg',
  fit: BoxFit.fill,
);
final Widget imageSchool_3 = Image.asset(
  'assets/images/school_3.jpg',
  fit: BoxFit.fill,
);
final Widget imageScience = Image.asset(
  'assets/images/science.jpg',
  fit: BoxFit.fill,
);

final List<Widget> imageList = [
  imageAtom,
  imageBackToSchool,
  imageBrain,
  imageBrainstorm,
  imageBulbs,
  imageExhausted,
  imageLearn,
  imageMan_1,
  imageMan_2,
  imageOffice,
  imagePaper,
  imageQuestions,
  imageReading,
  imageSchool_1,
  imageSchool_2,
  imageSchool_3,
  imageScience
];
