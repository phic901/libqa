DualEditor.markup.SYNTAX = function(contents){
    var idx = 0;
    function replacer(match, p1, p2, offset, string) {
        var type = ["applescript","as3","shell","cf","c","c#","css","delphi","diff","erl","groovy","java","javafx","js","pl","php","text","py","ruby","sass","scala","sql","vb","html"];
        var data = syntaxData[idx];
        p1 = p1.replace(/</gm, "&lt;");
        data = data.replace(/<br>/ig, "");
        for( var i=0; i< type.length; i++ ){
            if( type[i] == p1 ){
                return "<pre class=\"brush: "+p1+"\">"+data+"</pre>";
            }
        }
        idx++;
        return data.replace(/</gm, "&lt;");
    }
    contents = contents.replace(/\[syntax ([\w\W]+?)\]([\w\W]+?)\[syntax\]/gm, replacer);
	return contents;
};

DualEditor.markup.SYNTAX_BEFORE = function(contents){
    var idx = 0;
    function replacer(match, p1, p2, offset, string) {
        syntaxData.push(p2);
        idx++;
        return "[syntax "+p1+"]  [syntax]";
    }
    contents.replace(/\[syntax ([\w\W]+?)\]([\w\W]+?)\[syntax\]/gm, replacer);
    return contents;
};
