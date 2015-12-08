/**
 * GotIt React Client
 * EIrik Kvarstein
 */
'use strict';

var React = require('react-native');
var DDP = require('ddp.js');
var Toolbar = require('./components/Toolbar.js');
var Input = require('./components/Input.js');
var LectureSelector = require('./components/LectureSelector.js');
var QuestionList = require('./components/QuestionList.js');

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
        endpoint: "http://eirik.pw:3000/websocket",
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
    console.log("Result: ",data);
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
    console.log("Asking question: ", args);
    this.state.ddp.method("questionInsertAddVote", [args], function(e, err){
      if(err){
        console.log("error: "+err);
      }
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
    var content = null;
    if(this.state.inLecture){
      content =  (<View style={styles.content}>
                    <Text>Status: {this.state.status}</Text>
                    <QuestionList dataSource={this.state.dataSource}/>
                    <Input ask={this.askQuestion}/>
                  </View>);
    }else {
      content = (<View style={styles.content}>
                  <LectureSelector onClick={this.enterLecture} />
                </View>);
    }
      return (
        <View style={styles.mainContainer}>
          <Toolbar code={this.state.currentLectureCode}/>
            {content}
        </View>
      );

  }
});

var styles = StyleSheet.create({
  mainContainer: {
    flex: 1,
  },
  content:{
    flex: 1,
    backgroundColor:'#ebeef0',
  },
  header: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  question: {
    fontSize: 25,
  },

});



AppRegistry.registerComponent('GotItApp', () => GotItApp);
