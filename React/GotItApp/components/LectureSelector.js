
'use strict';

var React = require('react-native');
var {
  View,
  Text,
  TextInput,
  TouchableHighlight,
  StyleSheet
} = React;


var LectureSelector = React.createClass({
  getInitialState: function(){
    return{
      lectureCode : 'y1w4q',
    };
  },
  onClick: function(e){
    this.props.onClick(this.state.lectureCode);
  },
  render: function(){
    return(
      <View>
      <TextInput style={styles.Message} onChangeText={(text) => this.setState({lectureCode: text})} value={this.state.lectureCode}/>
        <TouchableHighlight onPress={this.onClick}>
          <Text style={styles.header}>Enter Lecture</Text>
        </TouchableHighlight>
    </View>
    );
  }
});

var styles = StyleSheet.create({
  Message: {
    height: 40,
    borderWidth: 1
  },
});

module.exports = LectureSelector;
