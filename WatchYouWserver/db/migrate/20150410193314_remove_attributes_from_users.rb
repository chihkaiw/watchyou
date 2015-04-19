class RemoveAttributesFromUsers < ActiveRecord::Migration
  def change
	remove_column :users, :scheduleID
  end
end
