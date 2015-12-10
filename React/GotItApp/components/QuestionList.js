
'use strict';

var React = require('react-native');
var {
  View,
  Text,
  TextInput,
  ListView,
  TouchableHighlight,
  StyleSheet
} = React;

var QuestionList = React.createClass({
  getInitialState: function(){
    return{
      dataSource: new ListView.DataSource({
        rowHasChanged: (row1, row2) => row1 !== row2,
        })
    };
  },
  componentWillReceiveProps: function(nextProps){
    console.log("component will receive");

    // BUG: Listview does not trigger update when this.props.questions changes..
    // So Added votes are never displayed.
    this.setState({
    dataSource: this.state.dataSource.cloneWithRows(this.props.questions)
  });
  },
  _pressRow: function(e){
    console.log("Voting question: "+e.questionText);
    this.props.voteHandler(e);
    console.log(JSON.stringify(e)); // {"lectureCode":"s27h7","questionText":"what is up","author":"X2fp5wxyj8BPYFeY9","submitted":{"$date":1449609000456},"votes":1}
  },
  renderQuestion: function(question, sectionID: number, rowID: number){
    console.log("render: "+JSON.stringify(question));
    return(
      <TouchableHighlight onPress={() => this._pressRow(question)}>
      <View style={styles.row}>

        <Text style={styles.question}>{question.questionText}</Text>
        <Text style={styles.votes}>{question.votes} votes</Text>
        <View style={styles.separator} />
    </View>
    </TouchableHighlight>
    );
  },
  render: function(){
    return(
      <ListView
      dataSource={this.state.dataSource}
      renderRow={this.renderQuestion}
    />
    );
  }
});

var styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    justifyContent: 'center',
    padding: 10,
    
  },
  separator: {
    height: 1,
    backgroundColor: '#CCCCCC',
  },
  thumb: {
    width: 64,
    height: 64,
  },
  question: {
    flex: 1,
  },
  votes:{
      width: 50,            //Step 2
      color:'#000',
      textAlign:'center'
  },
});

module.exports = QuestionList;
