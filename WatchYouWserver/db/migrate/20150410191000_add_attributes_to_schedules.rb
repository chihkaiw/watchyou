class AddAttributesToSchedules < ActiveRecord::Migration
  def change
	add_column :schedules, :title, :string
	add_column :schedules, :star, :string
	add_column :schedules, :note, :string
	add_column :schedules, :type, :string
	add_column :schedules, :userID, :string
  end
end
