var URL1_REG = new RegExp("(ftp|http|https|file):\\/\\/[a-zA-Z0-9-_\\/.\\:\\?=\\&]+(\\b|$)", "gim"); // URL starting with a protocol among these : ftp, http, https, file
var URL1_LINK = '<a href="$&" style="text-decoration:none" title="Open $& link" target="_blank"><em>$&</em></a>';
var URL2_REG = new RegExp("(^|[^\\/])(www[a-zA-Z0-9-_\\/.\\:\\?=\\&]+(\\b|$))", "gim"); // URL without protocol, starting with www. http is set as the default protocol
var URL2_LINK = '$1<a href="http://$2" style="text-decoration:none" title="Open http://$2 link" target="_blank"><em>$2</em></a>';

