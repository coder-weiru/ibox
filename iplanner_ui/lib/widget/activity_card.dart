import 'package:flutter/material.dart';
import 'package:flutter_tags/tag.dart';
import 'package:iplanner_ui/model/activity_list.dart';
import 'package:iplanner_ui/model/attributes.dart';

class ActivityCard extends StatelessWidget {
  final Activity _activity;

  const ActivityCard({@required activity}) : _activity = activity;

  Activity get activity => this._activity;

  Color getActivityColor() {
    Color color = Colors.white;
    TagAttribute tagAttribute = _activity.tags;
    if (tagAttribute != null) {
      Tag primaryTag = tagAttribute.getPrimaryTag();
      if (primaryTag != null && primaryTag.argbValue != null) {
        color = Color(int.parse(primaryTag.argbValue));
      }
    }
    return color;
  }

  Widget _buildTags(BuildContext context, TagAttribute tagAttribute) {
    Set<Tag> _tagSet = tagAttribute.tags ?? Set<Tag>();
    List<Tag> _items = _tagSet.toList();
    return Tags(
      itemCount: _items.length,
      itemBuilder: (int index) {
        final item = _items[index];
        return ItemTags(
            key: Key(index.toString()),
            index: index,
            title: item.value,
            textStyle: Theme.of(context).textTheme.overline,
            combine: ItemTagsCombine.withTextBefore);
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final title = _activity.title ?? "";
    final description = _activity.description ?? "";

    return Container(
        width: MediaQuery.of(context).size.width,
        margin: EdgeInsets.symmetric(horizontal: 10.0),
        decoration: new BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(25.0),
        ),
        child: Card(
            color: getActivityColor(),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(25.0),
            ),
            margin: EdgeInsets.symmetric(horizontal: 10.0, vertical: 10.0),
            elevation: 10,
            child: Padding(
              padding: const EdgeInsets.only(
                top: 10.0,
                bottom: 10.0,
                left: 10.0,
                right: 10.0,
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: <Widget>[
                  Text(title, style: Theme.of(context).textTheme.headline),
                  Text(description, style: Theme.of(context).textTheme.subhead),
                  new Divider(),
                  Visibility(
                      visible: _activity.tags != null,
                      child: _buildTags(context, _activity.tags)),
                ],
              ),
            )));
  }
}
