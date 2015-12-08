
'use strict';

var React = require('react-native');
var {
  View,
  Text,
  TextInput,
  ListView,
  StyleSheet
} = React;

var QuestionList = React.createClass({
  renderQuestion: function(question){
    return(
      <View style={styles.listItem}>
        <Text style={styles.question}>{question.questionText}</Text>
      </View>
    );
  },
  render: function(){
    return(
      <ListView
      dataSource={this.props.dataSource}
      renderRow={this.renderQuestion}
      style={styles.listView}
    />
    );
  }
});

var styles = StyleSheet.create({
  listItem: {
   flex: 1,
   flexDirection: 'row',
   backgroundColor: '#F5FCFF',
 },
 listView: {
   paddingTop: 20,
   backgroundColor: '#F5FCFF',
 },
});

module.exports = QuestionList;
