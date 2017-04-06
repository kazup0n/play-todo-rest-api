require 'airborne'

def endpoint(path)
	"http://localhost:9000/#{path}"
end


describe '/taskList' do
  before do
    #全てのリストを削除
    get endpoint("taskList")
    json_body.each do |tasklist|
      delete endpoint("taskList/#{tasklist[:id]}")
    end
    get endpoint("taskList")
    expect_json_sizes(0)
  end

  it "作成、削除、更新を順に行い、整合性が取れている" do
    # リストを２つ作る
    post endpoint('taskList'), {name: 'ShoppingList'}
    post endpoint('taskList'), {name: 'ShoppingList 2'}
    get  endpoint('taskList')
    expect_json_sizes(2)
    task_lists = json_body
    # １つ消す
    delete endpoint("taskList/#{task_lists[0][:id]}")
    get endpoint('taskList')
    expect_json_sizes(1)
    #更新する
    param = task_lists[1]
    param[:name] = 'Foobar'
    patch endpoint("taskList/#{task_lists[1][:id]}"), param

    expect_json(name: 'Foobar')
  end

end

describe "/taskList/:id/tasks" do
  before do
      #全てのリストを削除
      get endpoint("taskList")
      json_body.each do |tasklist|
        delete endpoint("taskList/#{tasklist[:id]}")
      end
      get endpoint("taskList")
      expect_json_sizes(0)
  end

  it "作成、削除、更新を順に行い、整合性が取れている" do
    #リスト作成
    post endpoint('taskList'), {name: 'ShoppingList'}
    tasklist = json_body
    # タスクを２つ作成
    post endpoint("taskList/#{tasklist[:id]}/tasks"), {title: "milk", description: "Buy milk"}
    expect_json(title: 'milk', description: 'Buy milk', id: -> (id){ expect(id.length).to eq(36)})

    post endpoint("taskList/#{tasklist[:id]}/tasks"), {title: "choco", description: "Buy choco"}
    expect_json(title: 'choco', description: 'Buy choco', id: -> (id){ expect(id.length).to eq(36)})
    # タスクが２個ある?
    get endpoint("taskList/#{tasklist[:id]}")
    expect(json_body[:tasks].size).to eq(2)
    # 削除
    json_body[:tasks].each do |task|
      delete endpoint("taskList/#{tasklist[:id]}/tasks/#{task[:id]}")
    end
    # 空になっている
    get endpoint("taskList/#{tasklist[:id]}")
    expect(json_body[:tasks].size).to eq(0)
  end
end