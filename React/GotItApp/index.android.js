/**
 * GotIt React Client
 * EIrik Kvarstein
 */
'use strict';

var React = require('react-native');
var DDP = require('ddp.js');
var {
  AppRegistry,
  StyleSheet,
  Text,
  TextInput,
  View,
  TouchableHighlight,
  ListView
} = React;



var GotItApp = React.createClass({
  getInitialState: function(){
    var options = {
        endpoint: "http://192.168.11.192:3000/websocket",
        SocketConstructor: WebSocket
    };
    return{
        ddp: new DDP(options),
        status: "idle..",
        message: '',
        subId_questions: '',
        subId_votes: '',
        subId_lectures: '',
        questions: {},
        lectures: {},
        inLecture: false,
        currentLectureCode: '',
        dataSource: new ListView.DataSource({
          rowHasChanged: (row1, row2) => row1 !== row2,
          }),


    };
  },
  onConnected: function(){
    this.setState({status: "Connected"});
    //Subscribe

  },
  onAdded: function(data){
    this.setState({message: data.collection});

    switch(data.collection){
      case "lectures":
        var _lectures = this.state.lectures;
        _lectures[data.id] = data.fields;
          this.setState({
            lectures: _lectures
          });
        break;
      case "questions":
      var _questions = this.state.questions;
      _questions[data.id] = data.fields;
        this.setState({
          questions: _questions,
          status: data.id,
          dataSource: this.state.dataSource.cloneWithRows(this.state.questions)
        });
        break;

      case "votes":

        break;
    }
    //collection, id, fields
    // questions should be a hashmap<id, data>. map.put(data.id, data)

  },
  onChanged: function(data){

  },
  onRemoved: function(data){

  },
  onResult: function(data){

  },
  enterLecture: function(lectureCode){
    //subscribe to lectureCode
    this.state.ddp.sub("questions", [lectureCode]);
    this.setState({
      currentLectureCode: lectureCode,
      inLecture: true
    });
  },
  askQuestion: function(question){
  /*  var args = {};
    args.lectureCode = this.state.currentLectureCode;
    args.questionText = question; */
    var args = {
    lectureCode: this.state.currentLectureCode,
    questionText: question
};
    this.state.ddp.method("questionInsertAddVote", [args], function(e, err){

    });
  },
  componentDidMount: function(){
    //Setup DDP event handlers
    this.state.ddp.on("connected", this.onConnected);
    this.state.ddp.on("added", this.onAdded);
    this.state.ddp.on("changed", this.onChanged);
    this.state.ddp.on("removed", this.onRemoved);
    this.state.ddp.on("result", this.onResult);

    // Subscribe / should be in onCOnnected?
    //this.state.ddp.sub("lecture");
    //this.state.ddp.sub("questions", ["s70t1"]);
    this.setState({ message: "Ready.."})
  },
  render: function(){
    if(this.state.inLecture){
      return (
        <View style={styles.container}>
          <Text style={styles.header}>
            Status: {this.state.status}
          </Text>
          <QuestionList dataSource={this.state.dataSource}/>
          <Input ask={this.askQuestion}/>
        </View>
      );
  }else {
      return(
        <LectureSelector onClick={this.enterLecture} />
      );
    }
  }
});


var LectureSelector = React.createClass({
  getInitialState: function(){
    return{
      lectureCode : 's70t1',
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
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  listItem: {
   flex: 1,
   flexDirection: 'row',
   backgroundColor: '#F5FCFF',
 },
 listView: {
   paddingTop: 20,
   backgroundColor: '#F5FCFF',
 },
  header: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  question: {
    fontSize: 25,
  },
  Message: {
    height: 40,
    borderWidth: 1
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

AppRegistry.registerComponent('GotItApp', () => GotItApp);
