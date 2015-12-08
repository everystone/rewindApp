
'use strict';

var React = require('react-native');
var {
  View,
  Text,
  TextInput,
  TouchableHighlight,
  StyleSheet
} = React;



var Input = React.createClass({
  getInitialState: function(){
    return{
      Message : '',
    };
  },
  _onClick: function(e){
    // ASk Question
    this.props.ask(this.state.Message);
    this.setState({ Message: '' });
  },
  render: function(){
    return(
      <View>
      <TextInput style={styles.Message} onChangeText={(text) => this.setState({Message: text})} value={this.state.Message}/>
        <TouchableHighlight onPress={this._onClick}>
          <Text>Ask Question</Text>
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

module.exports = Input;
