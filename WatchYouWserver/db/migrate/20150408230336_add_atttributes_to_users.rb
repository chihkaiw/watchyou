class AddAtttributesToUsers < ActiveRecord::Migration
  def change
	add_column :users, :name, :string
	add_column :users, :email, :string
	add_column :users, :password, :string
	add_column :users, :scheduleID, :string
	add_column :users, :id, :integer, :SERIAL
  end
end
